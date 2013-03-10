package com.interzonedev.twitterstackdemo.base;

public interface Receiver<Req, Resp> {

	public Resp receive(Req request);

}
