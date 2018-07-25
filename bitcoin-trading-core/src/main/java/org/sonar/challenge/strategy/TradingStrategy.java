package org.sonar.challenge.strategy;

import org.sonar.challenge.order.OrderBatch;

public interface TradingStrategy {

	OrderBatch resolveOrders();
	
}
