package org.sonar.challenge.rest;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.sonar.challenge.book.json.TradeResultDecoder;
import org.sonar.challenge.exception.RESTResponseNotSuccessException;
import org.sonar.challenge.util.GSonBuilder;

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

	public static void main(String[] args) {

		Runnable r = () -> {
			String response;
			try {
				response = new BitsoTradesRESTRequest("btc_mxn", 10).request();

				TradeResultDecoder tradeResult = GSonBuilder.buildStandardGson().fromJson(response,
						TradeResultDecoder.class);
				System.out.println(LocalDateTime.now() + " " + response);
			} catch (RESTResponseNotSuccessException e) {
				e.printStackTrace();
			}
		};
		Executors.newScheduledThreadPool(2).scheduleAtFixedRate(r, 1, 5, TimeUnit.SECONDS);

	}
}
