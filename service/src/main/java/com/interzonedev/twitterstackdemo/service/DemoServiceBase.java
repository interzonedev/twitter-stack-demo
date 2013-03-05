package com.interzonedev.twitterstackdemo.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.http.AbstractHttpServiceBase;
import com.interzonedev.twitterstackdemo.base.http.HttpUtils;
import com.interzonedev.twitterstackdemo.common.DemoApi;

@Named("demoServiceBase")
public class DemoServiceBase extends AbstractHttpServiceBase {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("demoService")
	private DemoApi demoService;

	@Value("#{serviceProperties.serverName}")
	private String serviceName;

	@Value("#{serviceProperties.hostName}")
	private String serviceHostName;

	@Value("#{serviceProperties.port}")
	private int servicePort;

	@PostConstruct
	public void init() {

		log.info("Launching service");

		launch();

	}

	@Override
	protected String getServiceName() {
		return serviceName;
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
	protected HttpResponse call(HttpRequest request) {

		log.debug("call: Received request - " + request);

		String content = null;
		HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;

		try {
			// Get method arguments from request parameters.
			Map<String, List<String>> parameters = HttpUtils.getParametersFromRequest(request);

			String message = "";
			if (null != parameters.get("message")) {
				message = parameters.get("message").get(0);
			}

			long delayMillis = 0L;
			if (null != parameters.get("delayMillis")) {
				String delayMillsValue = parameters.get("delayMillis").get(0);
				try {
					delayMillis = Long.parseLong(delayMillsValue);
				} catch (NumberFormatException nfe) {
					log.warn("call: Error converting " + delayMillsValue + " to a long");
				}
			}

			content = demoService.doSomething(message, delayMillis);
			status = HttpResponseStatus.OK;
		} catch (Throwable t) {
			log.error("call: Error executing service", t);
			content = "Error executing service";
			if (StringUtils.isNotBlank(t.getMessage())) {
				content += ":" + t.getMessage();
			}
		}

		HttpResponse response = HttpUtils.buildResponse(status, null, content);

		log.debug("call: Returning response - " + response);

		return response;

	}

}
