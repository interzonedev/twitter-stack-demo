package com.interzonedev.twitterstackdemo.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

public class FunctionalTestDemoClient extends AbstractTest {

	@Inject
	@Named("demoClient")
	private DemoClient demoClient;

	@Test
	public void testMakeRequest() throws InterruptedException {

		log.debug("testMakeRequest: Start");

		String responseContent = demoClient.doRequest("foo");

		log.debug("testMakeRequest: responseContent = " + responseContent);

		Assert.assertTrue(responseContent.contains("currentTimeMillis"));
		Assert.assertTrue(responseContent.contains("parameters"));
		Assert.assertTrue(responseContent.contains("headers"));

	}

}
