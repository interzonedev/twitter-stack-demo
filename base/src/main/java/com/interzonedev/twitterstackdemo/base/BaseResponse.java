package com.interzonedev.twitterstackdemo.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Top level immutable value object for representing responses independent of the transport mechanism.
 * 
 * @author mmarkarian
 */
public class BaseResponse {

	private final BaseRequest request;

	private final Map<String, List<String>> headers;

	private final byte[] content;

	public BaseResponse(BaseRequest request, Map<String, List<String>> headers, byte[] content) {
		this.request = request;

		if (null == headers) {
			this.headers = Collections.emptyMap();
		} else {
			this.headers = Collections.unmodifiableMap(headers);
		}

		this.content = content;
	}

	public BaseRequest getRequest() {
		return request;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public byte[] getContent() {
		return content;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("from ").append(getRequest());

		return sb.toString();
	}

}
