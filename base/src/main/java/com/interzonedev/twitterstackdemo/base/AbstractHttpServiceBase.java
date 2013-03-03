package com.interzonedev.twitterstackdemo.base;

import java.net.InetSocketAddress;

import javax.inject.Named;

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
 * Simple implementation of {@link Service<HttpRequest, HttpResponse>} that echos the request parameters back to the
 * response as JSON.
 * 
 * @author mmarkarian
 */
@Named("demoService")
public abstract class AbstractHttpServiceBase extends Service<HttpRequest, HttpResponse> {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	/**
	 * Starts the service.
	 */
	protected void launch() {

		String serviceName = getServiceName();

		String serviceHostName = getServiceHostName();

		int servicePort = getServicePort();

		log.info("launch: Starting service \"" + serviceName + "\" at http://" + serviceHostName + ":" + servicePort);

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

	protected abstract String getServiceName();

	protected abstract String getServiceHostName();

	protected abstract int getServicePort();

	protected abstract HttpResponse call(HttpRequest request);

}
