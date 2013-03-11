package com.interzonedev.twitterstackdemo.client;

import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.interzonedev.twitterstackdemo.common.DemoApi;

import ch.qos.logback.classic.Logger;

/**
 * Abstract super class for all functional tests on the client implementation of {@link DemoApi}. These are live tests
 * that send actual requests over the wire and require the service end to be running.
 * 
 * @author interzone
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/interzonedev/twitterstackdemo/client/applicationContext.xml" })
public abstract class AbstractTest {

	protected final Logger log = (Logger) LoggerFactory.getLogger(getClass());

}
