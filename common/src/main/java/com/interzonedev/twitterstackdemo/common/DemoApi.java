package com.interzonedev.twitterstackdemo.common;

/**
 * Common interface for the client and service of the demo app.
 * 
 * @author interzone
 */
public interface DemoApi {

	/**
	 * Echos the specified message and current system time after delaying for the specified number of milliseconds.
	 * 
	 * @param message
	 *            The message to echo back.
	 * @param delayMillis
	 *            The number of milliseconds to delay the current {@link Thread}.
	 * 
	 * @return Returns a serialized JSON object containing the specified message and the current system time.
	 * 
	 * @throws Exception
	 */
	public String doSomething(String message, long delayMillis) throws Exception;

	/**
	 * Logs the specified message.
	 * 
	 * @param message
	 *            The message to log.
	 * 
	 * @throws Exception
	 */
	public void doAnotherThing(String message) throws Exception;

}
