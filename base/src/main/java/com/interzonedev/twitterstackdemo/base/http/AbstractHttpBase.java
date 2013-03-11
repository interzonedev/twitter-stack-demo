package com.interzonedev.twitterstackdemo.base.http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * Abstract super class for all client and service implementations using HTTP as the transport mechanism. Contains
 * constants and helper methods common to both the client and the service.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpBase {

	public static final String SEND_REQUEST_HEADER_NAME = "base-send-request";

	protected final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	protected BaseHttpRequest addSendHeaderToRequest(BaseHttpRequest baseRequestIn) {

		Map<String, List<String>> headers = new HashMap<String, List<String>>(baseRequestIn.getHeaders());
		headers.put(SEND_REQUEST_HEADER_NAME, null);

		BaseHttpRequest baseRequestOut = new BaseHttpRequest(baseRequestIn.getId(), headers,
				baseRequestIn.getContent(), baseRequestIn.getUrl(), baseRequestIn.getMethod(),
				baseRequestIn.getParameters());

		return baseRequestOut;

	}
}
