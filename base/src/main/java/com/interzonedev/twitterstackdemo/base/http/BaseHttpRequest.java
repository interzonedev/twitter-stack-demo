package com.interzonedev.twitterstackdemo.base.http;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.interzonedev.twitterstackdemo.base.BaseRequest;

/**
 * Immutable value object for representing HTTP requests.
 * 
 * @author mmarkarian
 */
public class BaseHttpRequest extends BaseRequest {

	private final String url;

	private final BaseHttpMethod method;

	private final Map<String, List<String>> parameters;

	public BaseHttpRequest(String id, Map<String, List<String>> headers, byte[] content, String url,
			BaseHttpMethod method, Map<String, List<String>> parameters) {

		super(id, headers, content);

		if (StringUtils.isBlank(url)) {
			throw new IllegalArgumentException("The url must be set");
		}

		if (null == method) {
			throw new IllegalArgumentException("The method must be set");
		}

		this.url = url;
		this.method = method;

		if (null == parameters) {
			this.parameters = Collections.emptyMap();
		} else {
			this.parameters = Collections.unmodifiableMap(parameters);
		}

	}

	public String getUrl() {
		return url;
	}

	public BaseHttpMethod getMethod() {
		return method;
	}

	public Map<String, List<String>> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getId()).append(" - ").append(getMethod()).append(" ").append(getUrl());

		return sb.toString();
	}

}
