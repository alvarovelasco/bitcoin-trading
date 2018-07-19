package org.sonar.challenge.book.net;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.websocket.BitsoSubscriber;

public class CreateDiffOrderForBookUpdateCommandContext {

	private OrderBook orderBook;
	
	private BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber;
	
	public CreateDiffOrderForBookUpdateCommandContext() {
	}
	
	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}

	public OrderBook getOrderBook() {
		return orderBook;
	}
	
	public void setBitsoSubscriber(BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber) {
		this.bitsoSubscriber = bitsoSubscriber;
	}
	
	public BitsoSubscriber<DiffOrderDecoder> getBitsoSubscriber() {
		return bitsoSubscriber;
	}
}
