package com.interzonedev.twitterstackdemo.base.http.finagle;

import java.net.InetSocketAddress;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

import com.interzonedev.twitterstackdemo.base.Invoker;
import com.interzonedev.twitterstackdemo.base.Receiver;
import com.interzonedev.twitterstackdemo.base.ServiceBase;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;

/**
 * <a href="http://twitter.github.com/finagle" target="_blank">Twitter Finagle</a> specific implementation of
 * {@link ServiceBase}.
 * 
 * @author interzone
 */
public class FinagleHttpServiceBase extends AbstractFinagleHttpBase implements
		ServiceBase<BaseHttpRequest, BaseHttpResponse> {

	private final String serviceName;

	private final String serviceHostName;

	private final int servicePort;

	private final Invoker<BaseHttpRequest, BaseHttpResponse> invoker;

	private final Receiver<BaseHttpRequest, BaseHttpResponse> receiver;

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
				BaseHttpRequest baseRequest = getBaseHttpRequest(request);

				BaseHttpResponse baseResponse = null;

				if (isSendRequest(baseRequest)) {
					baseResponse = receiver.receive(baseRequest);
				} else {
					baseResponse = invoker.invoke(baseRequest);
				}

				response = getHttpResponse(baseResponse);
			} catch (Throwable t) {
				log.error("apply: Error calling service", t);
				response = buildResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, null, t.getMessage());
			}

			Future<HttpResponse> future = Future.value(response);

			log.debug("apply: Returning response - " + response);

			return future;

		}

	};

	public FinagleHttpServiceBase(String serviceName, String serviceHostName, int servicePort,
			Invoker<BaseHttpRequest, BaseHttpResponse> invoker, Receiver<BaseHttpRequest, BaseHttpResponse> receiver) {
		this.serviceName = serviceName;
		this.serviceHostName = serviceHostName;
		this.servicePort = servicePort;
		this.invoker = invoker;
		this.receiver = receiver;
	}

	/**
	 * Starts the service.
	 */
	@Override
	public void launch() {

		log.info("launch: Starting service \"" + serviceName + "\" at http://" + serviceHostName + ":" + servicePort);

		ServerBuilder.safeBuild(
				httpService,
				ServerBuilder.get().codec(Http.get()).name(serviceName)
						.bindTo(new InetSocketAddress(serviceHostName, servicePort)));

	}

}
