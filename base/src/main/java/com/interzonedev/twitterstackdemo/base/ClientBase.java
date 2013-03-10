package com.interzonedev.twitterstackdemo.base;

import com.twitter.util.Future;

public interface ClientBase<Req, Resp> {

	public void init();

	public Future<Resp> call(Req request);

	public Future<Resp> send(Req request);

}
