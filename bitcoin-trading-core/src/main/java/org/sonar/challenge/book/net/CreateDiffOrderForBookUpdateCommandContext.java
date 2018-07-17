package org.sonar.challenge.book.net;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.websocket.BitsoSubscriber;

public class CreateDiffOrderForBookUpdateCommandContext {

	private OrderBook orderBook;
	
	private BitsoSubscriber bitsoSubscriber;
	
	public void setOrderBook(OrderBook orderBook) {
		this.orderBook = orderBook;
	}

	public OrderBook getOrderBook() {
		return orderBook;
	}
	
	public void setBitsoSubscriber(BitsoSubscriber bitsoSubscriber) {
		this.bitsoSubscriber = bitsoSubscriber;
	}
	
	public BitsoSubscriber getBitsoSubscriber() {
		return bitsoSubscriber;
	}
}
