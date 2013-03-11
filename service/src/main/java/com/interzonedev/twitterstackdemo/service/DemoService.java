package com.interzonedev.twitterstackdemo.service;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interzonedev.twitterstackdemo.common.DemoApi;
import com.interzonedev.twitterstackdemo.service.transport.DemoInvoker;
import com.interzonedev.twitterstackdemo.service.transport.DemoReceiver;

/**
 * Service implementation of {@link DemoApi}. Performs the business logic whether called directly or via a remote
 * procedure call. Meant to be injected into {@link DemoInvoker} and {@link DemoReceiver}.
 * 
 * @author interzone
 * 
 */
@Named("demoService")
public class DemoService implements DemoApi {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	private final ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interzonedev.twitterstackdemo.common.DemoApi#doSomething(java.lang.String, long)
	 */
	@Override
	public String doSomething(String message, long delayMillis) throws Exception {

		log.debug("doSomething: Start");

		log.debug("doSomething: Sleep for " + delayMillis + " ms");
		Thread.sleep(delayMillis);
		log.debug("doSomething: Done sleeping");

		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("currentTimeMillis", System.currentTimeMillis());
		responseMap.put("message", message);

		String responseContent = objectMapper.writeValueAsString(responseMap);

		log.debug("doSomething: End");

		return responseContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interzonedev.twitterstackdemo.common.DemoApi#doAnotherThing(java.lang.String)
	 */
	@Override
	public void doAnotherThing(String message) throws Exception {

		log.debug("doAnotherThing: Received message = " + message);

	}

}
