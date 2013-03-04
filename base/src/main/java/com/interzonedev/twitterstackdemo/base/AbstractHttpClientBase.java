package com.interzonedev.twitterstackdemo.base;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * Abstract super class for all client implementations using HTTP as the transport mechanism.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpClientBase {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private Service<HttpRequest, HttpResponse> httpClient;

	/**
	 * Gets the necessary parameters from the implementing subclass and creates the client.
	 */
	protected void create() {

		String serviceHostName = getServiceHostName();

		int servicePort = getServicePort();

		int hostConnectionLimit = getHostConnectionLimit();

		log.info("create: Creating client for http://" + serviceHostName + ":" + servicePort
				+ " with connection limit " + hostConnectionLimit);

		httpClient = ClientBuilder.safeBuild(ClientBuilder.get().codec(Http.get())
				.hosts(serviceHostName + ":" + servicePort).hostConnectionLimit(hostConnectionLimit));

	}

	/**
	 * Allows the implementing subclass to specify the host name of the service to which this client connects.
	 * 
	 * @return Returns the host name of the service to which this client connects.
	 */
	protected abstract String getServiceHostName();

	/**
	 * Allows the implementing subclass to specify the port of the service to which this client connects.
	 * 
	 * @return Returns the port of the service to which this client connects.
	 */
	protected abstract int getServicePort();

	/**
	 * Allows the implementing subclass to specify the host connection limit to the service to which this client
	 * connects.
	 * 
	 * @return Returns the host connection limit of this client.
	 */
	protected abstract int getHostConnectionLimit();

	/**
	 * Meant to be called by the implementing subclass to perform an asynchronous HTTP request.
	 * 
	 * @param request
	 *            The {@link HttpRequest} to be sent.
	 * 
	 * @return Returns a {@link Future<HttpResponse>} that allows for asynchronously getting the response.
	 */
	protected Future<HttpResponse> call(HttpRequest request) {

		log.debug("call: Sending request - " + request);

		Future<HttpResponse> responseFuture = httpClient.apply(request);

		log.debug("call: Sent request");

		return responseFuture;

	}

}
