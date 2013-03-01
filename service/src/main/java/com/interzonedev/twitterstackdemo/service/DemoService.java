package com.interzonedev.twitterstackdemo.service;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * Simple implementation of {@link Service<HttpRequest, HttpResponse>} that echos the request parameters back to the
 * response as JSON.
 * 
 * @author mmarkarian
 */
@Named("demoService")
public class DemoService extends Service<HttpRequest, HttpResponse> {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Value("#{serviceProperties.serverName}")
	private String serviceName;

	@Value("#{serviceProperties.hostName}")
	private String serviceHostName;

	@Value("#{serviceProperties.port}")
	private int servicePort;

	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Starts the service.
	 */
	@PostConstruct
	public void init() {

		log.info("init: Starting service \"" + serviceName + "\" at http://" + serviceHostName + ":" + servicePort);

		ServerBuilder.safeBuild(
				this,
				ServerBuilder.get().codec(Http.get()).name(serviceName)
						.bindTo(new InetSocketAddress(serviceHostName, servicePort)));

	}

	/**
	 * Services requests. Returns a response with a JSON body that echos any parameters in the request.
	 * 
	 * @param request
	 *            The incoming {@link HttpRequest}.
	 * 
	 * @return Returns a {@link Future<HttpResponse>} that contains a JSON body that echos any parameters in the
	 *         request.
	 */
	@Override
	public Future<HttpResponse> apply(HttpRequest request) {

		log.debug("apply: Received request - " + request);

		String responseContent = null;
		HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;

		try {
			Map<String, List<String>> parameters = getParametersMap(request);
			Map<String, List<String>> headers = getHeadersMap(request);

			Map<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("currentTimeMillis", System.currentTimeMillis());
			responseMap.put("parameters", parameters);
			responseMap.put("headers", headers);

			responseContent = objectMapper.writeValueAsString(responseMap);
			status = HttpResponseStatus.OK;
		} catch (Throwable t) {
			if (StringUtils.isNotBlank(t.getMessage())) {
				responseContent = t.getMessage();
			}
			log.error("apply: Error generating response", t);
		}

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

		if (null != responseContent) {
			response.setContent(ChannelBuffers.wrappedBuffer(responseContent.getBytes()));
		}

		Future<HttpResponse> future = Future.value(response);

		log.debug("apply: Returning response - " + response);

		return future;

	}

	/**
	 * Transforms the parameters in either body or the query string of the specified {@link HttpRequest} into a map.
	 * 
	 * @param request
	 *            The current {@link HttpRequest}.
	 * 
	 * @return Returns a map containing the parameters in either body or the query string of the specified
	 *         {@link HttpRequest}.
	 */
	private Map<String, List<String>> getParametersMap(HttpRequest request) {

		Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();

		StringBuilder parameters = new StringBuilder();

		long contentLength = HttpHeaders.getContentLength(request);

		if (contentLength > 0) {
			parameters.append(request.getContent().toString(Charset.defaultCharset()));
		}

		String requestUri = request.getUri();

		if (requestUri.contains("?")) {
			if (parameters.length() > 0) {
				parameters.append("&");
			}
			parameters.append(requestUri.substring(requestUri.indexOf("?") + 1));
		}

		if (parameters.length() > 0) {
			String[] parameterParts = parameters.toString().split("&");

			for (String parameterPart : parameterParts) {
				String[] parameter = parameterPart.split("=");
				String name = parameter[0];
				String value = "";
				if (parameter.length > 1) {
					value = parameter[1];
				}

				List<String> mappedValues = parametersMap.get(name);
				if (null == mappedValues) {
					mappedValues = new ArrayList<String>();
					parametersMap.put(name, mappedValues);
				}
				mappedValues.add(value);
			}
		}

		return parametersMap;

	}

	private Map<String, List<String>> getHeadersMap(HttpRequest request) {

		Map<String, List<String>> headersMap = new HashMap<String, List<String>>();

		List<Map.Entry<String, String>> headers = request.getHeaders();

		for (Map.Entry<String, String> header : headers) {
			String headerName = header.getKey();
			String headerValue = header.getValue();

			List<String> mappedValues = headersMap.get(headerName);
			if (null == mappedValues) {
				mappedValues = new ArrayList<String>();
				headersMap.put(headerName, mappedValues);
			}
			mappedValues.add(headerValue);
		}

		return headersMap;

	}

}
