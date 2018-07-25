package org.sonar.challenge.rest;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class BitsoTradesRESTRequest extends BaseBitsoRESTRequest {

	private final String book;
	
	private final int limit;
	
	private final static String ENDPOINT = "https://api.bitso.com/v3/trades";
	
	private final static String BOOK = "book";
	
	private final static String LIMIT = "limit";
	
	public BitsoTradesRESTRequest(String book, int limit) {
		this.book = requireNonNull(book);
		this.limit = limit;
	}
	
	@Override
	List<NameValuePair> getRequestParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(BOOK, book));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(limit)));
		
		return params;
	}

	@Override
	String getEndpoint() {
		return ENDPOINT;
	}

	
}
