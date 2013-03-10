package com.interzonedev.twitterstackdemo.base;

public interface Invoker<Req, Resp> {

	public Resp invoke(Req request);

}
