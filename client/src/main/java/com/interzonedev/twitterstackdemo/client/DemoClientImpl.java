package com.interzonedev.twitterstackdemo.client;

import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.http.Http;
import com.twitter.util.FutureEventListener;

@Named("demoClient")
public class DemoClientImpl implements DemoClient {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private Service<HttpRequest, HttpResponse> client;

	@PostConstruct
	public void init() {

		client = ClientBuilder.safeBuild(ClientBuilder.get().codec(Http.get()).hosts("localhost:10000")
				.hostConnectionLimit(1));

	}

	@Override
	public String makeRequest(String message) {

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");

		FutureEventListener<HttpResponse> futureEventListener = new FutureEventListener<HttpResponse>() {

			private String responseContent;

			@Override
			public void onFailure(Throwable t) {
				log.error("Error getting response", t);
			}

			@Override
			public void onSuccess(HttpResponse response) {
				responseContent = response.getContent().toString(Charset.defaultCharset());
			}

		};

		client.apply(request).addEventListener(futureEventListener);

		return null;
	}

}
