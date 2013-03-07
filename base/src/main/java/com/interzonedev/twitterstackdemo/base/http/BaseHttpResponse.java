package com.interzonedev.twitterstackdemo.base.http;

import java.util.List;
import java.util.Map;

import com.interzonedev.twitterstackdemo.base.BaseRequest;
import com.interzonedev.twitterstackdemo.base.BaseResponse;

/**
 * Immutable value object for representing HTTP responses.
 * 
 * @author mmarkarian
 */
public class BaseHttpResponse extends BaseResponse {

	private final int status;

	public BaseHttpResponse(BaseRequest request, Map<String, List<String>> headers, String content, int status) {
		super(request, headers, content);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getStatus()).append(" from ").append(getRequest());

		return sb.toString();
	}

}
