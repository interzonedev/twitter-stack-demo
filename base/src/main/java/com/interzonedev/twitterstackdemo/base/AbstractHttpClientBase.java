package com.interzonedev.twitterstackdemo.base;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Duration;
import com.twitter.util.Try;

/**
 * Abstract super class for all client implementations using HTTP as the transport mechanism.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpClientBase {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private Service<HttpRequest, HttpResponse> client;

	/**
	 * Starts the service.
	 */
	protected void create() {

		String serviceHostName = getServiceHostName();

		int servicePort = getServicePort();

		int hostConnectionLimit = getHostConnectionLimit();

		log.info("create: Creating client for http://" + serviceHostName + ":" + servicePort
				+ " with connection limit " + hostConnectionLimit);

		client = ClientBuilder.safeBuild(ClientBuilder.get().codec(Http.get())
				.hosts(serviceHostName + ":" + servicePort).hostConnectionLimit(hostConnectionLimit));

	}

	protected abstract String getServiceHostName();

	protected abstract int getServicePort();

	protected abstract int getHostConnectionLimit();

	protected Try<HttpResponse> call(HttpRequest request, long timeoutNanos) {

		log.debug("call: Sending request - " + request);

		Try<HttpResponse> responseTry = client.apply(request).get(new Duration(timeoutNanos));

		log.debug("call: Sent request");

		return responseTry;

	}

}
