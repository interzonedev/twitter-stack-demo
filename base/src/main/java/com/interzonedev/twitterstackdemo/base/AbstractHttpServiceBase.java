package com.interzonedev.twitterstackdemo.base;

import java.net.InetSocketAddress;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * Abstract super class for all service implementations using HTTP as the transport mechanism.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpServiceBase {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	/**
	 * Anonymous implementation of the abstract {@link Service} class to receive, delegate and return HTTP requests.
	 */
	private final Service<HttpRequest, HttpResponse> httpService = new Service<HttpRequest, HttpResponse>() {

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

			HttpResponse response = null;

			try {
				response = call(request);
			} catch (Throwable t) {
				log.error("apply: Error calling service", t);
				HttpUtils.buildResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, t.getMessage());
			}

			Future<HttpResponse> future = Future.value(response);

			log.debug("apply: Returning response - " + response);

			return future;

		}

	};

	/**
	 * Gets the necessary parameters from the implementing subclass and starts the service.
	 */
	protected void launch() {

		String serviceName = getServiceName();

		String serviceHostName = getServiceHostName();

		int servicePort = getServicePort();

		log.info("launch: Starting service \"" + serviceName + "\" at http://" + serviceHostName + ":" + servicePort);

		ServerBuilder.safeBuild(
				httpService,
				ServerBuilder.get().codec(Http.get()).name(serviceName)
						.bindTo(new InetSocketAddress(serviceHostName, servicePort)));

	}

	/**
	 * Allows the implementing subclass to specify the service name.
	 * 
	 * @return Returns the name of the service managed by this instance.
	 */
	protected abstract String getServiceName();

	/**
	 * Allows the implementing subclass to specify the service host name.
	 * 
	 * @return Returns the hostname of the service managed by this instance.
	 */
	protected abstract String getServiceHostName();

	/**
	 * Allows the implementing subclass to specify the service port.
	 * 
	 * @return Returns the port of the service managed by this instance.
	 */
	protected abstract int getServicePort();

	/**
	 * Delegates the work to respond to thr request to the implementing subclass.
	 * 
	 * @param request
	 *            The current {@link HttpRequest}.
	 * 
	 * @return Returns the {@link HttpResponse} created by the implementing subclass.
	 */
	protected abstract HttpResponse call(HttpRequest request);

}
