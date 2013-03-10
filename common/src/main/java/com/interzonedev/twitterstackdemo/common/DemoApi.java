package com.interzonedev.twitterstackdemo.common;

public interface DemoApi {

	public String doSomething(String message, long delayMillis) throws Exception;

	public void doAnotherThing(String message) throws Exception;

}
