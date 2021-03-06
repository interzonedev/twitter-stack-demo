package com.interzonedev.twitterstackdemo.client;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.Test;

import com.interzonedev.twitterstackdemo.common.DemoApi;

/**
 * Functional tests on the client implementation of {@link DemoApi}.
 * 
 * @author interzone
 */
public class FunctionalTestDemoClient extends AbstractTest {

	@Inject
	@Named("demoClient")
	private DemoApi demoClient;

	@Test
	public void testDoSomething() throws Exception {

		log.debug("testDoSomething: Start");

		String responseContent = demoClient.doSomething("Foo", 500);

		log.debug("testDoSomething: responseContent = " + responseContent);

		Assert.assertTrue(responseContent.contains("currentTimeMillis"));
		Assert.assertTrue(responseContent.contains("message"));

	}

	@Test
	public void testDoAnotherThing() throws Exception {

		log.debug("testDoAnotherThing: Start");

		demoClient.doAnotherThing("Foo");

	}

}
