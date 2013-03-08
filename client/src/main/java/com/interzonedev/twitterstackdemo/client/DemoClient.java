package com.interzonedev.twitterstackdemo.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.http.BaseHttpMethod;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.interzonedev.twitterstackdemo.common.DemoApi;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.Throw;
import com.twitter.util.Try;

@Named("demoClient")
public class DemoClient implements DemoApi {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("demoClientBase")
	private DemoClientBase demoClientBase;

	@SuppressWarnings("unchecked")
	@Override
	public String doSomething(String message, long delayMillis) throws Exception {

		log.debug("doSomething: message = " + message + " - delayMillis = " + delayMillis);

		String url = "/";
		BaseHttpMethod method = BaseHttpMethod.GET;

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("message", Arrays.asList(new String[] { message }));
		parameters.put("delayMillis", Arrays.asList(new String[] { Long.toString(delayMillis) }));

		log.debug("doSomething: Sending request");

		Future<BaseHttpResponse> responseFuture = demoClientBase.call(url, method, null, parameters);

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

}
