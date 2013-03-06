package com.interzonedev.twitterstackdemo.base.http;

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
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * Abstract super class for all client and service implementations using HTTP as the transport mechanism. Contains
 * helper methods common to both the client and the service.
 * 
 * @author mmarkarian
 */
public abstract class AbstractHttpBase {

	private static final HttpVersion HTTP_VERSION = HttpVersion.HTTP_1_1;

	protected final Logger log = (Logger) LoggerFactory.getLogger(getClass());

	/**
	 * Creates an {@link BaseHttpRequest} instance from the properties in the specified {@link HttpRequest}.
	 * 
	 * @param request
	 *            The {@link HttpRequest} to use as the basis of creating the {@link BaseHttpRequest} instance.
	 * 
	 * @return Returns an {@link BaseHttpRequest} instance created from the properties in the specified
	 *         {@link HttpRequest}.
	 */
	protected BaseHttpRequest getBaseHttpRequest(HttpRequest request) {

		// TODO - Need to figure out how to pass around the request ID.
		String id = Integer.toString(request.hashCode());

		String url = request.getUri();

		BaseHttpMethod method = BaseHttpMethod.valueOf(request.getMethod().getName());

		Map<String, List<String>> headers = getHeadersFromMessage(request);

		Map<String, List<String>> parameters = getParametersFromRequest(request);

		String content = null;

		switch (method) {
			case POST:
			case PUT:
				content = getRequestContentFromParameters(parameters);
				break;
		}

		BaseHttpRequest baseRequest = new BaseHttpRequest(id, headers, content, url, method, parameters);

		return baseRequest;

	}

	/**
	 * Creates an {@link HttpResponse} instance from the properties in the specified {@link BaseHttpResponse}.
	 * 
	 * @param baseResponse
	 *            The {@link BaseHttpResponse} to use as the basis of creating the {@link HttpResponse} instance.
	 * 
	 * @return Returns an {@link HttpResponse} instance created from the properties in the specified
	 *         {@link BaseHttpResponse}.
	 */
	protected HttpResponse getHttpResponse(BaseHttpResponse baseResponse) {

		HttpResponseStatus status = HttpResponseStatus.valueOf(baseResponse.getStatus());

		Map<String, List<String>> headers = baseResponse.getHeaders();

		String content = baseResponse.getContent();

		HttpResponse reponse = buildResponse(status, headers, content);

		return reponse;

	}

	/**
	 * Creates a query string from the specified map of parameters.
	 * 
	 * @param parameters
	 *            The map of parameters to transform into a query string.
	 * 
	 * @return Returns a query string created from the specified map of parameters.
	 */
	protected String getRequestContentFromParameters(Map<String, List<String>> parameters) {

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
	 * Creates a {@link HttpResponse} instance from the specified parameters.
	 * 
	 * @param status
	 *            - The {@link HttpResponseStatus} of the response.
	 * @param headers
	 *            - The headers that should be added to the response if not null.
	 * @param content
	 *            - The content to be set as the body of the response if not null.
	 * 
	 * @return Returns a {@link HttpResponse} instance created from the specified parameters.
	 */
	protected HttpResponse buildResponse(HttpResponseStatus status, Map<String, List<String>> headers, String content) {

		HttpResponse response = new DefaultHttpResponse(HTTP_VERSION, status);

		addHeadersToMessage(response, headers);

		setContentInResponse(response, content);

		return response;

	}

	/**
	 * Transforms the headers in the specified {@link HttpMessage} into a map.
	 * 
	 * @param message
	 *            The {@link HttpMessage} from which to get the headers.
	 * 
	 * @return Returns a map containing the headers in the specified {@link HttpMessage}.
	 */
	protected Map<String, List<String>> getHeadersFromMessage(HttpMessage message) {

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

	/**
	 * Adds the headers in the specified map to the specified {@link HttpMessage}.
	 * 
	 * @param message
	 *            The {@link HttpMessage} to which to add the headers.
	 * @param headers
	 *            The map of headers to add to the specified {@link HttpMessage}.
	 */
	protected void addHeadersToMessage(HttpMessage message, Map<String, List<String>> headers) {

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
	protected void setContentInResponse(HttpResponse response, String content) {

		if (StringUtils.isNotBlank(content)) {
			response.setContent(ChannelBuffers.wrappedBuffer(content.getBytes()));
		}

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
	protected Map<String, List<String>> getParametersFromRequest(HttpRequest request) {

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

}
