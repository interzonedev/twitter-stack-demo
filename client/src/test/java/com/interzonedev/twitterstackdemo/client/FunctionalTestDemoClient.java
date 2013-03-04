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
	public void testMakeRequest() throws Exception {

		log.debug("testMakeRequest: Start");

		String responseContent = demoClient.doSomething("Foo", 5000);

		log.debug("testMakeRequest: responseContent = " + responseContent);

		Assert.assertTrue(responseContent.contains("currentTimeMillis"));
		Assert.assertTrue(responseContent.contains("message"));

	}

}
