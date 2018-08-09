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
public class DefaultTradingStrategy implements TradingStrategy {

	private final TradeTickResolver tradeTickResolver;
	
	private final List<Trade> trades ;
	
	private final TickCounter tickCounter;
	
	public DefaultTradingStrategy(List<Trade> trades, TradeTickResolver tradeTickResolver,
			TickCounter tickCounter) {
		this.trades = Objects.requireNonNull(trades);
		this.tradeTickResolver = tradeTickResolver;
		this.tickCounter = tickCounter;
	}
	
	@Override
	public OrderBatch resolveOrders() {
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
				if (!tickCounter.countAndCheckCounterUnderLimit(tick.get())) {
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
	
	private OrderType getOrderType(Ticks tick) {
		Objects.requireNonNull(tick);
		if (Ticks.UPTICK.equals(tick)) {
			return OrderType.SELL;
		} else {
			return OrderType.BUY;
		}
	}
}
