package com.interzonedev.twitterstackdemo.base;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class BaseRequest {

	private final String id;

	private final Map<String, List<String>> headers;

	private final String content;

	public BaseRequest(String id, Map<String, List<String>> headers, String content) {

		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("The id must be set");
		}

		this.id = id;

		if (null == headers) {
			this.headers = Collections.emptyMap();
		} else {
			this.headers = Collections.unmodifiableMap(headers);
		}

		this.content = content;
	}

	public String getId() {
		return id;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getId());

		return sb.toString();
	}

}
