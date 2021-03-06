package com.interzonedev.twitterstackdemo.base;

import com.interzonedev.twitterstackdemo.base.concurrent.BaseFutureAdaptor;

/**
 * Common API for all clients.
 * 
 * @author interzone
 * 
 * @param <Req>
 *            Placeholder for the outgoing request.
 * @param <Resp>
 *            Placeholder for the ingoing response.
 */
public interface ClientBase<Req, Resp> {

	/**
	 * Performs any necessary intialization like creating a client or making a connection.
	 */
	public void init();

	/**
	 * Performs any necessary cleanup such as closing the client.
	 */
	public void destroy();

	/**
	 * Makes a full round trip remote procedure call with content in the response.
	 * 
	 * @param request
	 *            A general command instance that contains the necessary information to make the request.
	 * 
	 * @return Returns a {@link BaseFutureAdaptor<Resp>} that allows asynchronous access to the response from the remote
	 *         procedure call.
	 */
	public BaseFutureAdaptor<Resp> call(Req request);

	/**
	 * Sends a message that may return an acknowledgement but no content is expected in the response.
	 * 
	 * @param request
	 *            A general command instance that contains the necessary information to make the request.
	 * 
	 * @return Returns a {@link BaseFutureAdaptor<Resp>} that allows asynchronous access to the acknowledgement from the
	 *         remote procedure call.
	 */
	public BaseFutureAdaptor<Resp> send(Req request);

}
