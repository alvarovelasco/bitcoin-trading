package org.sonar.challenge.book;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.sonar.challenge.order.Order;

/**
 * Contains the representation of an order book
 * 
 * @author Alvaro
 *
 */
public final class OrderBook {

	private final String name;
	
	private final List<Order> bids = new ArrayList<>();
	
	private final List<Order> asks = new ArrayList<>();
	
	private final long lastSequenceEntered;
	
	public OrderBook(String name, long sequence) {
		this.name = requireNonNull(name);
		this.lastSequenceEntered = sequence;
	}
	
	public void addBid(Order order) {
		bids.add(order);
	}
	
	public void addAsk(Order order) {
		asks.add(order);
	}

	public String getName() {
		return name;
	}
	
	public long getLastSequenceEntered() {
		return lastSequenceEntered;
	}
	
	public List<Order> getAsks() {
		return new ArrayList<Order>(asks);
	}
	
	public List<Order> getBids() {
		return new ArrayList<Order>(bids);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asks == null) ? 0 : asks.hashCode());
		result = prime * result + ((bids == null) ? 0 : bids.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (lastSequenceEntered ^ (lastSequenceEntered >>> 32));
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
		OrderBook other = (OrderBook) obj;
		if (asks == null) {
			if (other.asks != null)
				return false;
		} else if (!asks.equals(other.asks))
			return false;
		if (bids == null) {
			if (other.bids != null)
				return false;
		} else if (!bids.equals(other.bids))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (lastSequenceEntered != other.lastSequenceEntered)
			return false;
		return true;
	}
	

	@Override
	public String toString() {
		return "OrderBook [name=" + name + ", bids=" + bids + ", asks=" + asks + ", lastSequenceEntered="
				+ lastSequenceEntered + "]";
	}

}
