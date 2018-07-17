package org.sonar.challenge.rest;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

public class BitsoOrderBookRESTRequest implements SimpleRESTRequest {

	private final static String ENDPOINT = "https://api.bitso.com/v3/order_book";
	
	private final String orderBook;
	
	public BitsoOrderBookRESTRequest(String orderBook) {
		this.orderBook = requireNonNull(orderBook);
	}
	
	public String request() {
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		
		final List<Header> headers = Arrays.<Header> asList(new BasicHeader("accept", "application/json"));
		HttpClient httpClient = httpClientBuilder
				.setDefaultHeaders(headers).build();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("book", orderBook));
		params.add(new BasicNameValuePair("required", Boolean.FALSE.toString()));
		String paramString = URLEncodedUtils.format(params, "utf-8");

		HttpGet getMethod = new HttpGet(ENDPOINT + "?" + paramString);
		try {
			HttpResponse response = httpClient.execute(getMethod);
	
			if (response.getStatusLine().getStatusCode() != 200) {
				// FIXME AVF: Change the exception for handling
				throw new RuntimeException("Failed : HTTP error code : "
				   + response.getStatusLine().getStatusCode());
			}
			
			String content = IOUtils.toString(response.getEntity().getContent());
			return content;
		} catch (IOException e) {
			// Catch and throw the exception 
			throw new RuntimeException("Temporary exception");
		}
	}

}
