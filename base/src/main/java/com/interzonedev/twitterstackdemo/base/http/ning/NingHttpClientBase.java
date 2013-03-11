package com.interzonedev.twitterstackdemo.base.http.ning;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.interzonedev.twitterstackdemo.base.ClientBase;
import com.interzonedev.twitterstackdemo.base.concurrent.BaseFutureAdaptor;
import com.interzonedev.twitterstackdemo.base.http.AbstractHttpBase;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpMethod;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpProviderConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;

public class NingHttpClientBase extends AbstractHttpBase implements ClientBase<BaseHttpRequest, BaseHttpResponse> {

	private AsyncHttpClient asyncHttpClient;

	private final String serviceHostName;

	private final int servicePort;

	private final int hostConnectionLimit;

	public NingHttpClientBase(String serviceHostName, int servicePort, int hostConnectionLimit) {
		this.serviceHostName = serviceHostName;
		this.servicePort = servicePort;
		this.hostConnectionLimit = hostConnectionLimit;
	}

	/**
	 * Build the client configuration and client.
	 */
	@Override
	public void init() {

		log.info("init: Creating client for http://" + serviceHostName + ":" + servicePort + " with connection limit "
				+ hostConnectionLimit);

		// Configure the Netty AsyncHttpProviderConfig. Can set things like whether or not to use non-blocking IO.
		AsyncHttpProviderConfig<String, Object> asyncHttpProviderConfig = new NettyAsyncHttpProviderConfig();

		// Configure the AsyncHttpClientConfig via its builder. Can set things like idle connection timeout, request
		// timeout, etc.
		AsyncHttpClientConfig.Builder asyncHttpClientConfigBuilder = new AsyncHttpClientConfig.Builder();
		asyncHttpClientConfigBuilder.setAsyncHttpClientProviderConfig(asyncHttpProviderConfig);
		asyncHttpClientConfigBuilder.setRequestTimeoutInMs(2000);
		asyncHttpClientConfigBuilder.setMaximumConnectionsTotal(hostConnectionLimit);

		AsyncHttpClientConfig asyncHttpClientConfig = asyncHttpClientConfigBuilder.build();

		// Use Netty as the concrete implementation of the transport layer.
		NettyAsyncHttpProvider nettyAsyncHttpProvider = new NettyAsyncHttpProvider(asyncHttpClientConfig);

		asyncHttpClient = new AsyncHttpClient(nettyAsyncHttpProvider, asyncHttpClientConfig);

	}

	/**
	 * Closes the client.
	 */
	@Override
	public void destroy() {

		if (!asyncHttpClient.isClosed()) {
			asyncHttpClient.close();
		}

	}

	@Override
	public BaseFutureAdaptor<BaseHttpResponse> call(final BaseHttpRequest request) {

		final String errorMessage = "Error making request";

		ListenableFuture<BaseHttpResponse> ningResponseFuture = null;

		try {

			BoundRequestBuilder requestBuilder = getRequestBuilderFromRequest(request);

			ningResponseFuture = requestBuilder.execute(new AsyncCompletionHandler<BaseHttpResponse>() {

				@Override
				public BaseHttpResponse onCompleted(com.ning.http.client.Response ningResponse) throws Exception {
					BaseHttpResponse response = transformResponse(request, ningResponse);
					return response;
				}

				@Override
				public void onThrowable(Throwable t) {
					log.error("call: " + errorMessage, t);
				}
			});

		} catch (Throwable t) {
			log.error("call: " + errorMessage, t);
		}

		return new NingFutureAdaptor(ningResponseFuture);

	}

	@Override
	public BaseFutureAdaptor<BaseHttpResponse> send(BaseHttpRequest baseRequest) {

		log.debug("send: Sending for send - baseRequest - " + baseRequest);

		BaseHttpRequest sendRequest = addSendHeaderToRequest(baseRequest);

		BaseFutureAdaptor<BaseHttpResponse> futureAdaptor = call(sendRequest);

		log.debug("send: Sent request");

		return futureAdaptor;

	}

	/**
	 * Assemble the {@link BoundRequestBuilder} instance that represents the HTTP request from the
	 * {@link BaseHttpRequest} value object.
	 * 
	 * @param request
	 *            The {@link BaseHttpRequest} value object that contains the components of the
	 *            {@link BoundRequestBuilder} to assemble.
	 * 
	 * @return Returns an {@link BoundRequestBuilder} instance that represents the HTTP request from the
	 *         {@link BaseHttpRequest} value object.
	 */
	private BoundRequestBuilder getRequestBuilderFromRequest(BaseHttpRequest request) {
		String url = "http://" + serviceHostName + ":" + servicePort + request.getUrl();

		BaseHttpMethod method = request.getMethod();

		BoundRequestBuilder requestBuilder = null;

		switch (method) {
			case GET:
				requestBuilder = asyncHttpClient.prepareGet(url);
				break;
			case POST:
				requestBuilder = asyncHttpClient.preparePost(url);
				break;
			case PUT:
				requestBuilder = asyncHttpClient.preparePut(url);
				break;
			case DELETE:
				requestBuilder = asyncHttpClient.prepareDelete(url);
				break;
			case OPTIONS:
				requestBuilder = asyncHttpClient.prepareOptions(url);
				break;
			case HEAD:
				requestBuilder = asyncHttpClient.prepareHead(url);
				break;
			case CONNECT:
				requestBuilder = asyncHttpClient.prepareConnect(url);
				break;
			default:
				throw new RuntimeException("Unsupported request method: " + method);
		}

		addRequestParametersToRequestBuilder(requestBuilder, request);

		addRequestHeadersToRequestBuilder(requestBuilder, request);

		return requestBuilder;

	}

	private void addRequestParametersToRequestBuilder(BoundRequestBuilder requestBuilder, BaseHttpRequest request) {

		BaseHttpMethod method = request.getMethod();

		Map<String, List<String>> parameters = request.getParameters();
		for (String parameterName : parameters.keySet()) {
			List<String> parameterValues = parameters.get(parameterName);
			for (String parameterValue : parameterValues) {

				switch (method) {
					case POST:
					case PUT:
						requestBuilder.addParameter(parameterName, parameterValue);
						break;
					default:
						requestBuilder.addQueryParameter(parameterName, parameterValue);
				}

			}
		}

	}

	private void addRequestHeadersToRequestBuilder(BoundRequestBuilder requestBuilder, BaseHttpRequest request) {
		Map<String, List<String>> headers = request.getHeaders();
		for (String headerName : headers.keySet()) {
			List<String> headerValues = headers.get(headerName);
			if (null != headerValues) {
				for (String headerValue : headerValues) {
					requestBuilder.addHeader(headerName, headerValue);
				}
			} else {
				requestBuilder.addHeader(headerName, "");
			}
		}
	}

	private BaseHttpResponse transformResponse(BaseHttpRequest request, Response ningResponse) throws IOException {

		int status = ningResponse.getStatusCode();

		Map<String, List<String>> headers = ningResponse.getHeaders();

		String responseContent = ningResponse.getResponseBody();
		byte[] content = null;
		if (StringUtils.isNotBlank(responseContent)) {
			content = responseContent.getBytes();
		}

		BaseHttpResponse response = new BaseHttpResponse(request, headers, content, status);

		return response;

	}
}
