package com.interzonedev.twitterstackdemo.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.ClientBase;
import com.interzonedev.twitterstackdemo.base.concurrent.BaseFutureAdaptor;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpMethod;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.interzonedev.twitterstackdemo.common.DemoApi;

/**
 * Client implementation of {@link DemoApi}. Creats the necessary instance of {@link BaseHttpRequest} to make the remote
 * service call for each method.
 * 
 * @author interzone
 */
@Named("demoClient")
public class DemoClient implements DemoApi {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("ningDemoHttpClientBase")
	private ClientBase<BaseHttpRequest, BaseHttpResponse> httpClientBase;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interzonedev.twitterstackdemo.common.DemoApi#doSomething(java.lang.String, long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String doSomething(String message, long delayMillis) throws Exception {

		log.debug("doSomething: message = " + message + " - delayMillis = " + delayMillis);

		String url = "/something";
		BaseHttpMethod method = BaseHttpMethod.GET;

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("message", Arrays.asList(new String[] { message }));
		parameters.put("delayMillis", Arrays.asList(new String[] { Long.toString(delayMillis) }));

		BaseHttpRequest baseRequest = new BaseHttpRequest(UUID.randomUUID().toString(), null, null, url, method,
				parameters);

		log.debug("doSomething: Sending request");

		BaseFutureAdaptor<BaseHttpResponse> futureAdaptor = httpClientBase.call(baseRequest);

		log.debug("doSomething: Sent request");

		log.debug("doSomething: Blocking the thread to get the response");

		long timeoutMillis = 1000L;

		String responseContent = null;

		try {
			BaseHttpResponse response = futureAdaptor.get(timeoutMillis, TimeUnit.MILLISECONDS);

			log.debug("doSomething: Got response");

			byte[] responseContentBytes = response.getContent();
			if (null != responseContentBytes) {
				responseContent = new String(responseContentBytes);
			}
		} catch (Throwable t) {
			log.error("doSomething: Error getting response", t);
			throw new Exception(t);
		}

		log.debug("doSomething: Returning response  - " + responseContent);

		return responseContent;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.interzonedev.twitterstackdemo.common.DemoApi#doAnotherThing(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doAnotherThing(String message) throws Exception {

		log.debug("doAnotherThing: message = " + message);

		String url = "/anotherThing";
		BaseHttpMethod method = BaseHttpMethod.GET;

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("message", Arrays.asList(new String[] { message }));

		BaseHttpRequest baseRequest = new BaseHttpRequest(UUID.randomUUID().toString(), null, null, url, method,
				parameters);

		log.debug("doAnotherThing: Sending request");

		BaseFutureAdaptor<BaseHttpResponse> futureAdaptor = httpClientBase.send(baseRequest);

		log.debug("doAnotherThing: Sent request");

		log.debug("doAnotherThing: Blocking the thread to get the response");

		int responseStatus = 500;

		long timeoutMillis = 1000L;
		try {
			BaseHttpResponse response = futureAdaptor.get(timeoutMillis, TimeUnit.MILLISECONDS);
			log.debug("doAnotherThing: Got response");
			responseStatus = response.getStatus();
		} catch (Throwable t) {
			log.error("doAnotherThing: Error getting response", t);
			throw new Exception(t);
		}

		log.debug("doAnotherThing: responseStatus = " + responseStatus);

	}

}
