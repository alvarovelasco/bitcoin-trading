package org.sonar.challenge.rest;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class BitsoOrderBookRESTRequest extends BaseBitsoRESTRequest  {

	private final String orderBook;
	
	private final static String ENDPOINT = "https://api.bitso.com/v3/order_book";
	
	private final static String BOOK = "book";
	
	private final static String REQUIRED = "required";
	
	public BitsoOrderBookRESTRequest(String orderBook) {
		this.orderBook = requireNonNull(orderBook);
	}
	
	@Override
	List<NameValuePair> getRequestParameters() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(BOOK, orderBook));
		params.add(new BasicNameValuePair(REQUIRED, Boolean.FALSE.toString()));
		
		return params;
	}

	@Override
	String getEndpoint() {
		return ENDPOINT;
	}

}
