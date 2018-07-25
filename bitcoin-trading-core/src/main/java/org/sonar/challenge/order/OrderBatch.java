package org.sonar.challenge.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrderBatch {

	private final List<Order> newOrdersToPlace;
	
	public OrderBatch(List<Order> orders) {
		this.newOrdersToPlace = Objects.requireNonNull(orders);
	}
	
	public List<Order> getNewOrdersToPlace() {
		return new ArrayList<>(newOrdersToPlace);
	}
}
