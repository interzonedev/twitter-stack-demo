package com.interzonedev.twitterstackdemo.base;

/**
 * Common API for all services.
 * 
 * @author interzone
 * 
 * @param <Req>
 *            Placeholder for the incoming request.
 * @param <Resp>
 *            Placeholder for the outgoing response.
 */
public interface ServiceBase<Req, Resp> {

	/**
	 * Starts the service.
	 */
	public void launch();

}
