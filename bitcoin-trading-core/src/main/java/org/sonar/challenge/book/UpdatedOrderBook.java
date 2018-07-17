package org.sonar.challenge.book;

import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * 
 * @author avf
 *
 */
public final class UpdatedOrderBook {

	private final OrderBook upToDateOrderBook;
	
	private final List<Order> newBids;
	
	private final List<Order> newAsks;
	
	public UpdatedOrderBook(OrderBook upToDateOrderBook, List<Order> newBids, List<Order> newAsks) {
		this.upToDateOrderBook = requireNonNull(upToDateOrderBook);
		this.newBids = requireNonNull(newBids);
		this.newAsks = requireNonNull(newAsks);
	}

	public OrderBook getUpToDateOrderBook() {
		return upToDateOrderBook;
	}
	
	public List<Order> getNewAsks() {
		return newAsks;
	}
	
	public List<Order> getNewBids() {
		return newBids;
	}

	@Override
	public String toString() {
		return "UpdatedOrderBook [upToDateOrderBook=" + upToDateOrderBook + ", newBids=" + newBids + ", newAsks="
				+ newAsks + "]";
	}
	
}
