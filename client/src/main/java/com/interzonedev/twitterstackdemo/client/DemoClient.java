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

import com.interzonedev.twitterstackdemo.base.http.BaseHttpMethod;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.interzonedev.twitterstackdemo.base.http.HttpClientBase;
import com.interzonedev.twitterstackdemo.common.DemoApi;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.Throw;
import com.twitter.util.Try;

@Named("demoClient")
public class DemoClient implements DemoApi {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("httpClientBase")
	private HttpClientBase httpClientBase;

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

		Future<BaseHttpResponse> responseFuture = httpClientBase.call(baseRequest);

		log.debug("doSomething: Sent request");

		log.debug("doSomething: Blocking the thread to get the response");

		long timeoutMillis = 1000L;
		Try<BaseHttpResponse> responseTry = responseFuture.get(new Duration(TimeUnit.MILLISECONDS
				.toNanos(timeoutMillis)));

		log.debug("doSomething: Got response");

		String responseContent = null;

		if (responseTry.isReturn()) {
			BaseHttpResponse response = responseTry.get();
			byte[] responseContentBytes = response.getContent();
			if (null != responseContentBytes) {
				responseContent = new String(responseContentBytes);
			}
		} else {
			Throwable t = ((Throw<BaseHttpResponse>) responseTry).e();
			log.error("doSomething: Error getting response", t);
			throw new Exception(t);
		}

		log.debug("doSomething: Returning response  - " + responseContent);

		return responseContent;

	}

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

		log.debug("doSomething: Sending request");

		Future<BaseHttpResponse> responseFuture = httpClientBase.send(baseRequest);

		log.debug("doSomething: Sent request");

		log.debug("doSomething: Blocking the thread to get the response");

		long timeoutMillis = 1000L;
		Try<BaseHttpResponse> responseTry = responseFuture.get(new Duration(TimeUnit.MILLISECONDS
				.toNanos(timeoutMillis)));

		log.debug("doSomething: Got response");

		int responseStatus = 500;

		if (responseTry.isReturn()) {
			BaseHttpResponse response = responseTry.get();
			responseStatus = response.getStatus();
		} else {
			Throwable t = ((Throw<BaseHttpResponse>) responseTry).e();
			log.error("doSomething: Error getting response", t);
			throw new Exception(t);
		}

		log.debug("doSomething: responseStatus = " + responseStatus);

	}

}
