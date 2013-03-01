package com.interzonedev.twitterstackdemo.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * Simple implementation of {@link Service<HttpRequest, HttpResponse>} that echos the request parameters as JSON.
 * 
 * @author mmarkarian
 */
@Named("demoService")
public class DemoService extends Service<HttpRequest, HttpResponse> {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private String serviceName = "HttpServer";

	private String serviceHostName = "localhost";

	private int servicePort = 10000;

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
	 * Services requests.
	 */
	@Override
	public Future<HttpResponse> apply(HttpRequest request) {

		log.debug("apply: Received request - " + request);

		Map<String, List<String>> parameters = getParametersMap(request);

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		response.setContent(ChannelBuffers.wrappedBuffer("foo".getBytes()));

		Future<HttpResponse> future = Future.value(response);

		log.debug("apply: Returning response - " + response);

		return future;

	}

	private Map<String, List<String>> getParametersMap(HttpRequest request) {

		Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();

		HttpMethod requestMethod = request.getMethod();

		if (HttpMethod.POST.equals(requestMethod) || HttpMethod.PUT.equals(requestMethod)) {
			OutputStream out = null;
			try {
				request.getContent().readBytes(out, 0);
			} catch (IOException ioe) {
				log.error("Error reading request body", ioe);
			}
		} else {
			String requestUri = request.getUri();

			if (!requestUri.contains("?")) {
				return parametersMap;
			}

			String queryString = requestUri.substring(requestUri.indexOf("?") + 1);

			String[] queryStringParts = queryString.split("&");

			for (String queryStringPart : queryStringParts) {
				String[] param = queryStringPart.split("=");
				String name = param[0];
				String value = "";
				if (param.length > 1) {
					value = param[1];
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

	/*
	 * private Map<String, List<String>> getParametersMap(HttpServletRequest request) { Map<String, List<String>>
	 * parametersMap = new HashMap<String, List<String>>();
	 * 
	 * @SuppressWarnings("unchecked") Map<String, String[]> rawParameterMap = request.getParameterMap(); for (String
	 * parameterName : rawParameterMap.keySet()) { String[] rawParameterValues = rawParameterMap.get(parameterName);
	 * List<String> parameterValues = Arrays.asList(rawParameterValues); parametersMap.put(parameterName,
	 * parameterValues); }
	 * 
	 * return parametersMap; }
	 */

	public static void main(String[] args) {

		(new DemoService()).init();

	}

}
