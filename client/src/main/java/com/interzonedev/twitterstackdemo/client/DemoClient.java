package com.interzonedev.twitterstackdemo.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;
import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.HttpUtils;
import com.interzonedev.twitterstackdemo.common.DemoApi;
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
		HttpMethod method = HttpMethod.POST;

		Map<String, List<String>> parameters = new HashMap<String, List<String>>();
		parameters.put("message", Arrays.asList(new String[] { message }));
		parameters.put("delayMillis", Arrays.asList(new String[] { Long.toString(delayMillis) }));

		HttpRequest request = HttpUtils.buildRequest(url, method, null, parameters);

		Try<HttpResponse> responseTry = demoClientBase.call(request);

		String responseContent = null;

		if (responseTry.isReturn()) {
			HttpResponse response = responseTry.get();
			responseContent = HttpUtils.getResponseContent(response);
		} else {
			Throwable t = ((Throw<HttpResponse>) responseTry).e();
			log.error("doSomething: Error getting response", t);
			throw new Exception(t);
		}

		log.debug("doSomething: Returning response  - " + responseContent);

		return responseContent;

	}

}
