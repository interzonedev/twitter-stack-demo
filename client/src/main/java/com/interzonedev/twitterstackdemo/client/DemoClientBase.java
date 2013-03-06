package com.interzonedev.twitterstackdemo.client;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.http.AbstractHttpClientBase;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpMethod;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.twitter.util.Future;

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

	protected Future<BaseHttpResponse> call(String url, BaseHttpMethod method, Map<String, List<String>> headers,
			Map<String, List<String>> parameters) {

		String id = Integer.toString(hashCode());

		BaseHttpRequest baseRequest = new BaseHttpRequest(id, headers, null, url, method, parameters);

		log.debug("call: Sending baseRequest - " + baseRequest);

		Future<BaseHttpResponse> baseResponseFuture = call(baseRequest);

		log.debug("call: Sent request");

		return baseResponseFuture;

	}
}
