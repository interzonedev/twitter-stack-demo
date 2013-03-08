package com.interzonedev.twitterstackdemo.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.http.AbstractHttpServiceBase;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
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
	protected BaseHttpResponse call(BaseHttpRequest request) {

		log.debug("call: Received request - " + request);

		String content = null;
		int status = 500;

		try {

			Map<String, List<String>> parameters = request.getParameters();

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
			status = 200;

		} catch (Throwable t) {

			log.error("call: Error executing service", t);
			content = "Error executing service";
			if (StringUtils.isNotBlank(t.getMessage())) {
				content += ":" + t.getMessage();
			}

		}

		byte[] contentBytes = null;
		if (StringUtils.isNotBlank(content)) {
			contentBytes = content.getBytes();
		}

		BaseHttpResponse response = new BaseHttpResponse(request, null, contentBytes, status);

		return response;
	}

}
