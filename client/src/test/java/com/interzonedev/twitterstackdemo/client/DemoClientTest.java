package com.interzonedev.twitterstackdemo.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

public class DemoClientTest extends AbstractTest {

	@Inject
	@Named("demoClient")
	private DemoClient demoClient;

	@Test
	public void testMakeRequest() {

		log.debug("testMakeRequest");

		demoClient.makeRequest("foo");

		Assert.assertTrue(true);

	}

}
