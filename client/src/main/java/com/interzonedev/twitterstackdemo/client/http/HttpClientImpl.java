package com.interzonedev.twitterstackdemo.client.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.Throw;
import com.twitter.util.Try;

@Deprecated
@Named("httpClient")
public class HttpClientImpl implements HttpClient {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Value("#{serviceProperties.hostName}")
	private String serviceHostName;

	@Value("#{serviceProperties.port}")
	private int servicePort;

	@Value("#{serviceProperties.hostConnectionLimit}")
	private int hostConnectionLimit;

	private Service<HttpRequest, HttpResponse> client;

	@Inject
	@Named("httpThreadPoolExecutor")
	private ExecutorService httpThreadPoolExecutor;

	private class CallableRequest implements Callable<HttpResponse> {

		private final HttpRequest request;
		private final long timeoutNanos;

		private CallableRequest(HttpRequest request, long timeoutNanos) {
			this.request = request;
			this.timeoutNanos = timeoutNanos;
		}

		@Override
		public HttpResponse call() throws Exception {

			log.debug("call: Sending request - " + request);

			Try<HttpResponse> responseTry = client.apply(request).get(new Duration(timeoutNanos));

			HttpResponse response = null;

			if (responseTry.isReturn()) {
				response = responseTry.get();
			} else {
				Throwable t = ((Throw<HttpResponse>) responseTry).e();
				log.error("call: Error getting response", t);
				throw new Exception(t);
			}

			log.debug("call: Returning response - " + response);

			return response;

		}

	}

	@PostConstruct
	public void init() {

		client = ClientBuilder.safeBuild(ClientBuilder.get().codec(Http.get())
				.hosts(serviceHostName + ":" + servicePort).hostConnectionLimit(hostConnectionLimit));

	}

	@Override
	public Future<HttpResponse> doRequest(HttpRequest request, long timeoutNanos) throws Exception {

		log.debug("doRequest: Starting request - " + request);

		CallableRequest callableRequest = new CallableRequest(request, timeoutNanos);

		Future<HttpResponse> responseFuture = httpThreadPoolExecutor.submit(callableRequest);

		log.debug("doRequest: End");

		return responseFuture;

	}

	@Override
	public HttpRequest buildRequest(String url, HttpMethod method, Map<String, List<String>> headers,
			Map<String, List<String>> parameters) {

		StringBuilder urlWithParams = new StringBuilder(url);

		String requestContent = getRequestContentFromParameters(parameters);

		ChannelBuffer requestContentBuffer = null;

		if (StringUtils.isNotBlank(requestContent)) {
			if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method)) {
				requestContentBuffer = ChannelBuffers.wrappedBuffer(requestContent.getBytes());
			} else {

				if (urlWithParams.indexOf("?") > 0) {
					urlWithParams.append("&");
				} else {
					urlWithParams.append("?");
				}
				urlWithParams.append(requestContent);
			}
		}

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, urlWithParams.toString());

		if (null != requestContentBuffer) {
			request.setContent(requestContentBuffer);
			request.addHeader(Names.CONTENT_LENGTH, requestContent.length());
		}

		if (null != headers) {
			for (String headerName : headers.keySet()) {
				List<String> headerValues = headers.get(headerName);
				for (String headerValue : headerValues) {
					request.addHeader(headerName, headerValue);
				}
			}
		}

		return request;

	}

	private String getRequestContentFromParameters(Map<String, List<String>> parameters) {

		if (null == parameters) {
			return null;
		}

		StringBuilder content = new StringBuilder();

		for (String name : parameters.keySet()) {
			List<String> values = parameters.get(name);
			for (String value : values) {
				content.append(name).append("=").append(value).append("&");
			}
		}

		if (content.length() > 0) {
			content.delete(content.length() - 1, content.length());
		}

		return content.toString();

	}
}
