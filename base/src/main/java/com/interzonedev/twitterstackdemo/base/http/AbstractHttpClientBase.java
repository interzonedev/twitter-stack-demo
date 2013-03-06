package com.interzonedev.twitterstackdemo.base.http;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * Abstract super class for all client implementations using HTTP as the transport mechanism.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpClientBase extends AbstractHttpBase {

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
	 * @param baseRequest
	 *            The {@link BaseHttpRequest} to be sent.
	 * 
	 * @return Returns a {@link Future<BaseHttpResponse>} that allows for asynchronously getting the response.
	 */
	protected Future<BaseHttpResponse> call(BaseHttpRequest baseRequest) {

		log.debug("call: Sending baseRequest - " + baseRequest);

		HttpRequest request = getHttpRequest(baseRequest);

		// TODO - Transform Future<HttpResponse> to Future<BaseHttpResponse>
		Future<HttpResponse> responseFuture = httpClient.apply(request);

		HttpResponse response = responseFuture.get();
		@SuppressWarnings("unused")
		BaseHttpResponse baseResponse = getBaseHttpResponse(response, baseRequest);

		Future<BaseHttpResponse> baseResponseFuture = null;

		log.debug("call: Sent request");

		return baseResponseFuture;

	}

}
