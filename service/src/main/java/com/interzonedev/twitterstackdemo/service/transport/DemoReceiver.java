package com.interzonedev.twitterstackdemo.service.transport;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.interzonedev.twitterstackdemo.base.Receiver;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpRequest;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.interzonedev.twitterstackdemo.common.DemoApi;

/**
 * Implementation of {@link Receiver<BaseHttpRequest, BaseHttpResponse>} specific to the service implementation of
 * {@link DemoApi}.
 * 
 * @author interzone
 */
@Named("demoReceiver")
public class DemoReceiver implements Receiver<BaseHttpRequest, BaseHttpResponse> {

	private final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	@Inject
	@Named("demoService")
	private DemoApi demoService;

	/**
	 * Determines which method to call on the service implementation of {@link DemoApi} based on the properties of the
	 * incomong {@link BaseHttpRequest}.
	 * 
	 * @param request
	 *            The incoming {@link BaseHttpRequest}.
	 * 
	 * @return Returns a {@link BaseHttpResponse} with no content and a 204 status after the method call on the service
	 *         implementation of {@link DemoApi}.
	 */
	@Override
	public BaseHttpResponse receive(BaseHttpRequest request) {

		log.debug("receive: Received request - " + request);

		int status = 500;

		try {

			Map<String, List<String>> parameters = request.getParameters();

			String message = "";
			if (null != parameters.get("message")) {
				message = parameters.get("message").get(0);
			}

			demoService.doAnotherThing(message);
			status = 204;

		} catch (Throwable t) {

			log.error("receive: Error executing service", t);

		}

		BaseHttpResponse response = new BaseHttpResponse(request, null, null, status);

		return response;

	}

}
