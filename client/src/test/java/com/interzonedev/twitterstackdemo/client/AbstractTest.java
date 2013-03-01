package com.interzonedev.twitterstackdemo.client;

import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/interzonedev/twitterstackdemo/client/applicationContext.xml" })
public abstract class AbstractTest {

	protected final Logger log = (Logger) LoggerFactory.getLogger(getClass());

}
