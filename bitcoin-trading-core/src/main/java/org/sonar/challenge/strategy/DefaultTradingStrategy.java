package org.sonar.challenge.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.sonar.challenge.book.Trade;
import org.sonar.challenge.order.OrderBatch;

// TODO AVF: Unit testing
public final class DefaultTradingStrategy implements TradingStrategy {

	private final List<Trade> trades ;
	
	public DefaultTradingStrategy(List<Trade> trades) {
		this.trades = Objects.requireNonNull(trades);
	}
	
	@Override
	public OrderBatch resolveOrders() {
		
		// FIXME Alvaro Change to trades
		return new OrderBatch(Collections.emptyList());
	}
	
	
}
