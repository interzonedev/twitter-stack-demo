package com.interzonedev.twitterstackdemo.service.runner;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.qos.logback.classic.Logger;

/**
 * Entry point for running the service from the command line via the {@link #main(String[])} method.
 * 
 * @author interzone
 */
public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	/**
	 * Loads and starts the service application context.
	 * 
	 * @param args
	 *            The array of arguments passed on the command line. Unused.
	 */
	public static void main(String[] args) {

		log.info("Loading service application context");

		new ClassPathXmlApplicationContext("/com/interzonedev/twitterstackdemo/service/applicationContext.xml");

	}

}
