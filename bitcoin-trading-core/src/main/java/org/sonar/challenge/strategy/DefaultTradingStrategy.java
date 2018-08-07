package org.sonar.challenge.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.sonar.challenge.book.Trade;
import org.sonar.challenge.order.Order;
import org.sonar.challenge.order.OrderBatch;
import org.sonar.challenge.order.OrderType;

// TODO AVF: Unit testing
public final class DefaultTradingStrategy implements TradingStrategy {

	private final int downticksLimit;
	
	private final int upticksLimit;
	
	private final TradeTickResolver tradeTickResolver;
	
	private final List<Trade> trades ;
	
	public DefaultTradingStrategy(List<Trade> trades, TradeTickResolver tradeTickResolver,
			int downticksLimit, int upticksLimit) {
		this.trades = Objects.requireNonNull(trades);
		this.tradeTickResolver = tradeTickResolver;
		this.downticksLimit = downticksLimit;
		this.upticksLimit = upticksLimit;
	}
	
	@Override
	public OrderBatch resolveOrders() {
		int generalCounter = 0;
		Ticks firstTick = null;
		boolean issueOrder = false;
		
		
		for (int counter = 0; (counter + 1) < trades.size(); counter ++) {
			Optional<Ticks> tick = tradeTickResolver.resolve(trades.get(counter), trades.get(counter + 1));
			if (tick.isPresent()) {
				if (firstTick == null) {
					firstTick = tick.get();
				}
				if (firstTick != null && !firstTick.equals(tick.get())) {
					break;
				}
				if (getLimit(tick.get()) == ++generalCounter) {
					issueOrder = true;
					break;
				}
			}
		}
		
		List<Order> orders = new ArrayList<>();
		if (issueOrder) {
			orders.add(new Order(trades.get(0).getPrice(), 
					BigDecimal.ONE, 
					getOrderType(firstTick),
					LocalDateTime.now()));
		}

		return new OrderBatch(orders);
	}
	
	private int getLimit(Ticks tick) {
		Objects.requireNonNull(tick);
		if (Ticks.UPTICK.equals(tick)) {
			return upticksLimit;
		} else {
			return downticksLimit;
		}
	}
	
	private OrderType getOrderType(Ticks tick) {
		Objects.requireNonNull(tick);
		if (Ticks.UPTICK.equals(tick)) {
			return OrderType.SELL;
		} else {
			return OrderType.BUY;
		}
	}
}
