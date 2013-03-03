package com.interzonedev.twitterstackdemo.base;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * Utility methods for manipulating {@link HttpRequest}s and {@link HttpResponse}s.
 * 
 * @author mmarkarian
 */
public class HttpUtils {

	private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

	public static HttpRequest buildRequest(String url, HttpMethod method, Map<String, List<String>> headers,
			Map<String, List<String>> parameters) {

		StringBuilder urlWithParams = new StringBuilder(url);

		String requestContent = getRequestContentFromParameters(parameters);

		ChannelBuffer requestContentBuffer = null;

		if (StringUtils.isNotBlank(requestContent)) {
			if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method)) {
				requestContentBuffer = ChannelBuffers.wrappedBuffer(requestContent.getBytes());
			} else {

				if (urlWithParams.indexOf("?") > 0) {
					urlWithParams.append("&");
				} else {
					urlWithParams.append("?");
				}
				urlWithParams.append(requestContent);
			}
		}

		HttpRequest request = new DefaultHttpRequest(HTTP_VERSION, method, urlWithParams.toString());

		if (null != requestContentBuffer) {
			request.setContent(requestContentBuffer);
			request.addHeader(Names.CONTENT_LENGTH, requestContent.length());
		}

		if (null != headers) {
			for (String headerName : headers.keySet()) {
				List<String> headerValues = headers.get(headerName);
				for (String headerValue : headerValues) {
					request.addHeader(headerName, headerValue);
				}
			}
		}

		return request;

	}

	public static String getRequestContentFromParameters(Map<String, List<String>> parameters) {

		if (null == parameters) {
			return null;
		}

		StringBuilder content = new StringBuilder();

		for (String name : parameters.keySet()) {
			List<String> values = parameters.get(name);
			for (String value : values) {
				content.append(name).append("=").append(value).append("&");
			}
		}

		if (content.length() > 0) {
			content.delete(content.length() - 1, content.length());
		}

		return content.toString();

	}

	/**
	 * Transforms the parameters in either body or the query string of the specified {@link HttpRequest} into a map.
	 * 
	 * @param request
	 *            The {@link HttpRequest} from which to get the parameters.
	 * 
	 * @return Returns a map containing the parameters in either body or the query string of the specified
	 *         {@link HttpRequest}.
	 */
	public static Map<String, List<String>> getParametersFromRequest(HttpRequest request) {

		Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();

		StringBuilder parameters = new StringBuilder();

		long contentLength = HttpHeaders.getContentLength(request);

		if (contentLength > 0) {
			parameters.append(request.getContent().toString(Charset.defaultCharset()));
		}

		String requestUri = request.getUri();

		if (requestUri.contains("?")) {
			if (parameters.length() > 0) {
				parameters.append("&");
			}
			parameters.append(requestUri.substring(requestUri.indexOf("?") + 1));
		}

		if (parameters.length() > 0) {
			String[] parameterParts = parameters.toString().split("&");

			for (String parameterPart : parameterParts) {
				String[] parameter = parameterPart.split("=");
				String name = parameter[0];
				String value = "";
				if (parameter.length > 1) {
					value = parameter[1];
				}

				List<String> mappedValues = parametersMap.get(name);
				if (null == mappedValues) {
					mappedValues = new ArrayList<String>();
					parametersMap.put(name, mappedValues);
				}
				mappedValues.add(value);
			}
		}

		return parametersMap;

	}

	/**
	 * Transforms the headers in the specified {@link HttpMessage} into a map.
	 * 
	 * @param message
	 *            The {@link HttpMessage} from which to get the headers.
	 * 
	 * @return Returns a map containing the headers in the specified {@link HttpMessage}.
	 */
	public static Map<String, List<String>> getHeadersFromMessage(HttpMessage message) {

		Map<String, List<String>> headersMap = new HashMap<String, List<String>>();

		List<Map.Entry<String, String>> headers = message.getHeaders();

		for (Map.Entry<String, String> header : headers) {
			String headerName = header.getKey();
			String headerValue = header.getValue();

			List<String> mappedValues = headersMap.get(headerName);
			if (null == mappedValues) {
				mappedValues = new ArrayList<String>();
				headersMap.put(headerName, mappedValues);
			}
			mappedValues.add(headerValue);
		}

		return headersMap;

	}

	public static void addHeadersToMessage(HttpMessage message, Map<String, List<String>> headers) {

		if (null != headers) {
			for (String headerName : headers.keySet()) {
				List<String> headerValues = headers.get(headerName);
				for (String headerValue : headerValues) {
					message.addHeader(headerName, headerValue);
				}
			}
		}

	}

	/**
	 * Sets the specified content as the body of the specified {@link HttpResponse}.
	 * 
	 * @param response
	 *            The {@link HttpResponse} in which to set the content.
	 * @param content
	 *            The content to set in the {@link HttpResponse}.
	 */
	public static void setContentInResponse(HttpResponse response, String content) {

		if (StringUtils.isNotBlank(content)) {
			response.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
		}

	}

	public static HttpResponse buildResponse(HttpResponseStatus status, Map<String, List<String>> headers,
			String content) {

		HttpResponse response = new DefaultHttpResponse(HTTP_VERSION, status);

		addHeadersToMessage(response, headers);

		setContentInResponse(response, content);

		return response;

	}

	public static String getResponseContent(HttpResponse response) {

		String responseContent = response.getContent().toString(Charset.defaultCharset());

		return responseContent;

	}

}
