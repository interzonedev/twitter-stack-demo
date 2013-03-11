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
	 * Performs any necessary intialization such as creating and starting the service.
	 */
	public void init();

	/**
	 * Performs any necessary cleanup such as closing the service.
	 */
	public void destroy();

}
