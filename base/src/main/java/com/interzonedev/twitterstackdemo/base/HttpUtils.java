package com.interzonedev.twitterstackdemo.base;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
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

		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

		addHeadersToMessage(response, headers);

		setContentInResponse(response, content);

		return response;

	}

}
