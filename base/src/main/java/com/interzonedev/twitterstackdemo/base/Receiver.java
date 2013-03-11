package com.interzonedev.twitterstackdemo.base;

/**
 * Common API for allowing an implementation of {@link ServiceBase} to call a specific method on a specific service
 * where response is expected (though an acknowledgement might be returned). Implementations determine which service
 * method to call depending on the properties of the incoming request. Instances implementing this interface are meant
 * to be injected into implementations of {@link ServiceBase}.
 * 
 * @author interzone
 * 
 * @param <Req>
 *            Placeholder for the incoming request.
 * @param <Resp>
 *            Placeholder for the outgoing response.
 */
public interface Receiver<Req, Resp> {

	/**
	 * Called by implementations of {@link ServiceBase} where no response is expected. Calls a specific method on a
	 * service implementation.
	 * 
	 * @param request
	 *            A general command instance that contains the necessary information to call a specific service method.
	 * 
	 * @return Returns a response that contains no content beyond a possible acknowledgement.
	 */
	public Resp receive(Req request);

}
