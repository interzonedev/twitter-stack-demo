package com.interzonedev.twitterstackdemo.base;

/**
 * Common API for all services.
 * 
 * @author interzone
 * 
 * @param <Req>
 * @param <Resp>
 */
public interface ServiceBase<Req, Resp> {

	/**
	 * Starts the service.
	 */
	public void launch();

}
