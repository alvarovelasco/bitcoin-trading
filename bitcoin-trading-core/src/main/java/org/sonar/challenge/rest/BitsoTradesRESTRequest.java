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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((book == null) ? 0 : book.hashCode());
		result = prime * result + limit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BitsoTradesRESTRequest other = (BitsoTradesRESTRequest) obj;
		if (book == null) {
			if (other.book != null)
				return false;
		} else if (!book.equals(other.book))
			return false;
		if (limit != other.limit)
			return false;
		return true;
	}

}
