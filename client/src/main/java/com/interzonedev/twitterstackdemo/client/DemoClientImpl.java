package com.interzonedev.twitterstackdemo.client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.client.http.HttpClient;

@Named("demoClient")
public class DemoClientImpl implements DemoClient {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("httpClient")
	private HttpClient httpClient;

	@SuppressWarnings("unchecked")
	@Override
	public String doRequest(String message) {

		log.debug("makeRequest: Sending message = " + message);

		String url = "/";
		HttpMethod method = HttpMethod.POST;

		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		headers.put("h1", Arrays.asList(new String[] { "v1" }));

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("message", Arrays.asList(new String[] { message }));

		HttpRequest request = httpClient.buildRequest(url, method, headers, parameters);
		long timeoutNanos = TimeUnit.SECONDS.toNanos(1);

		String responseContent = null;
		try {
			Future<HttpResponse> responseFuture = httpClient.doRequest(request, timeoutNanos);

			HttpResponse response = responseFuture.get();
			responseContent = response.getContent().toString(Charset.defaultCharset());

			log.debug("makeRequest: Received response - status = " + response.getStatus().getCode() + " - content = "
					+ responseContent);
		} catch (Throwable t) {
			log.error("Error getting response", t);
		}

		return responseContent;

	}

}
