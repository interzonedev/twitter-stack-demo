package com.interzonedev.twitterstackdemo.base.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * @param <V>
 * 
 * @author mmarkarian
 */
public interface BaseFutureAdaptor<V> {

	public V get() throws Exception;

	public V get(long timeout, TimeUnit unit) throws Exception;

}
