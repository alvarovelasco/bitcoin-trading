package org.sonar.challenge.rest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.sonar.challenge.exception.RESTResponseNotSuccessException;

public abstract class BaseBitsoRESTRequest implements SimpleRESTRequest {

	private final static String CHARSET = "utf-8";
	
	abstract List<NameValuePair> getRequestParameters();
	
	abstract String getEndpoint();
	
	public final String request() throws RESTResponseNotSuccessException {
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		
		final List<Header> headers = Arrays.<Header> asList(new BasicHeader("accept", "application/json"));
		HttpClient httpClient = httpClientBuilder
				.setDefaultHeaders(headers).build();
		
		String paramString = URLEncodedUtils.format(getRequestParameters(), CHARSET);

		HttpGet getMethod = new HttpGet(getEndpoint() + "?" + paramString);
		try {
			HttpResponse response = httpClient.execute(getMethod);
	
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RESTResponseNotSuccessException(response.getStatusLine());
			}
			
			String content = IOUtils.toString(response.getEntity().getContent(), Charset.forName(CHARSET));
			
			return content;
		} catch (IOException e) {
			// Catch and throw the exception 
			throw new RuntimeException("Temporary exception");
		}
	}

}
