package com.interzonedev.twitterstackdemo.client;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.AbstractHttpClientBase;
import com.twitter.util.Try;

@Named("demoClientBase")
public class DemoClientBase extends AbstractHttpClientBase {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Value("#{serviceProperties.hostName}")
	private String serviceHostName;

	@Value("#{serviceProperties.port}")
	private int servicePort;

	@Value("#{serviceProperties.hostConnectionLimit}")
	private int hostConnectionLimit;

	@PostConstruct
	public void init() {

		log.info("init: Creating client");

		create();

	}

	@Override
	protected String getServiceHostName() {
		return serviceHostName;
	}

	@Override
	protected int getServicePort() {
		return servicePort;
	}

	@Override
	protected int getHostConnectionLimit() {
		return hostConnectionLimit;
	}

	protected Try<HttpResponse> call(HttpRequest request) {

		log.debug("call: Sending request - " + request);

		long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(3000);
		Try<HttpResponse> responseTry = call(request, timeoutNanos);

		log.debug("call: Sent request");

		return responseTry;

	}

}
