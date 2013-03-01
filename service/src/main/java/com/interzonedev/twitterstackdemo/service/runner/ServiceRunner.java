package com.interzonedev.twitterstackdemo.service.runner;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.qos.logback.classic.Logger;

public class ServiceRunner {

	private static final Logger log = (Logger) LoggerFactory.getLogger(ServiceRunner.class);

	public static void main(String[] args) {

		log.info("Loading service application context");

		new ClassPathXmlApplicationContext("/com/interzonedev/twitterstackdemo/service/applicationContext.xml");

	}

}
