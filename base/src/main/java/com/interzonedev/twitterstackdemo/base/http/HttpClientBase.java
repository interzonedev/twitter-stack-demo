package com.interzonedev.twitterstackdemo.base.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import com.interzonedev.twitterstackdemo.base.ClientBase;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.Future;
import com.twitter.util.FutureTransformer;

public class HttpClientBase extends AbstractHttpBase implements ClientBase<BaseHttpRequest, BaseHttpResponse> {

	private final String serviceHostName;

	private final int servicePort;

	private final int hostConnectionLimit;

	private Service<HttpRequest, HttpResponse> httpClient;

	public HttpClientBase(String serviceHostName, int servicePort, int hostConnectionLimit) {
		this.serviceHostName = serviceHostName;
		this.servicePort = servicePort;
		this.hostConnectionLimit = hostConnectionLimit;
	}

	@Override
	public void init() {
		log.info("init: Creating client for http://" + serviceHostName + ":" + servicePort + " with connection limit "
				+ hostConnectionLimit);

		httpClient = ClientBuilder.safeBuild(ClientBuilder.get().codec(Http.get())
				.hosts(serviceHostName + ":" + servicePort).hostConnectionLimit(hostConnectionLimit));

	}

	@Override
	public Future<BaseHttpResponse> call(final BaseHttpRequest baseRequest) {

		log.debug("call: Sending for call - baseRequest - " + baseRequest);

		HttpRequest request = getHttpRequest(baseRequest);

		Future<HttpResponse> responseFuture = httpClient.apply(request);

		Future<BaseHttpResponse> baseResponseFuture = responseFuture
				.transformedBy(new FutureTransformer<HttpResponse, BaseHttpResponse>() {
					@Override
					public BaseHttpResponse map(HttpResponse response) {
						log.debug("FutureTransformer.map: Received response - " + response);
						BaseHttpResponse baseResponse = getBaseHttpResponse(response, baseRequest);
						return baseResponse;
					}
				});

		log.debug("call: Sent request");

		return baseResponseFuture;

	}

	@Override
	public Future<BaseHttpResponse> send(BaseHttpRequest baseRequest) {

		log.debug("send: Sending for send - baseRequest - " + baseRequest);

		Map<String, List<String>> headers = new HashMap<String, List<String>>(baseRequest.getHeaders());
		headers.put(SEND_REQUEST_HEADER_NAME, null);

		BaseHttpRequest sendRequest = new BaseHttpRequest(baseRequest.getId(), headers, baseRequest.getContent(),
				baseRequest.getUrl(), baseRequest.getMethod(), baseRequest.getParameters());

		Future<BaseHttpResponse> baseResponseFuture = call(sendRequest);

		log.debug("send: Sent request");

		return baseResponseFuture;

	}

}
