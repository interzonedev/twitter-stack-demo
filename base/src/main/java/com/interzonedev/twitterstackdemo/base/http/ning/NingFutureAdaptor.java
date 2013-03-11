package com.interzonedev.twitterstackdemo.base.http.ning;

import java.util.concurrent.TimeUnit;

import com.interzonedev.twitterstackdemo.base.concurrent.BaseFutureAdaptor;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.ning.http.client.ListenableFuture;

public class NingFutureAdaptor implements BaseFutureAdaptor<BaseHttpResponse> {

	private final ListenableFuture<BaseHttpResponse> future;

	public NingFutureAdaptor(ListenableFuture<BaseHttpResponse> future) {
		this.future = future;
	}

	@Override
	public BaseHttpResponse get() throws Exception {
		return future.get();
	}

	@Override
	public BaseHttpResponse get(long timeout, TimeUnit unit) throws Exception {
		return future.get(timeout, unit);
	}

}
