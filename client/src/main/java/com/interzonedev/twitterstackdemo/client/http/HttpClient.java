package com.interzonedev.twitterstackdemo.client.http;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public interface HttpClient {

	public Future<HttpResponse> doRequest(HttpRequest request, long timeoutNanos) throws Exception;

	public HttpRequest buildRequest(String url, HttpMethod method, Map<String, List<String>> headers,
			Map<String, List<String>> parameters);

}
