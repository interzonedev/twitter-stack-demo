package com.interzonedev.twitterstackdemo.base.http.finagle;

import java.util.concurrent.TimeUnit;

import com.interzonedev.twitterstackdemo.base.concurrent.BaseFutureAdaptor;
import com.interzonedev.twitterstackdemo.base.http.BaseHttpResponse;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.Throw;
import com.twitter.util.Try;

public class FinagleFutureAdaptor implements BaseFutureAdaptor<BaseHttpResponse> {

	private final Future<BaseHttpResponse> responseFuture;

	public FinagleFutureAdaptor(Future<BaseHttpResponse> responseFuture) {
		this.responseFuture = responseFuture;
	}

	@Override
	public BaseHttpResponse get() throws Exception {
		return responseFuture.get();
	}

	@Override
	public BaseHttpResponse get(long timeout, TimeUnit unit) throws Exception {
		Try<BaseHttpResponse> responseTry = responseFuture.get(new Duration(unit.toNanos(timeout)));
		if (responseTry.isReturn()) {
			return responseTry.get();
		} else {
			Throwable t = ((Throw<BaseHttpResponse>) responseTry).e();
			throw new Exception(t);
		}
	}

}
