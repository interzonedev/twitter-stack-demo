package com.interzonedev.twitterstackdemo.base;

/**
 * Common API for allowing an implementation of {@link ServiceBase} to call a specific method on a specific service
 * where a response with content is expected. Implementations determine which service method to call depending on the
 * properties of the incoming request.
 * 
 * @author interzone
 * 
 * @param <Req>
 *            Placeholder for the incoming request.
 * @param <Resp>
 *            Placeholder for the outgoing response.
 */
public interface Invoker<Req, Resp> {

	/**
	 * Called by implementations of {@link ServiceBase} where a response with content is expected. Calls a specific
	 * method on a service implementation.
	 * 
	 * @param request
	 *            A general command instance that contains the necessary information to call a specific service method.
	 * 
	 * @return Returns a response that contains the information returned by the specific service method that was called.
	 */
	public Resp invoke(Req request);

}
