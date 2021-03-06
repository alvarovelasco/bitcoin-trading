package org.sonar.challenge.order;

import java.util.ArrayList;
import java.util.List;

public final class DoNothingOrderIssuerImpl implements OrderIssuer {

	private List<OrderIssuerListener> listeners = new ArrayList<>();
	
	@Override
	public void addListener(OrderIssuerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void issue(OrderBatch orderBatch) {
		if (orderBatch != null) {
			orderBatch.getNewOrdersToPlace().stream().filter(o -> o != null).forEach(o -> {
				listeners.stream().forEach(l -> l.beforeIssuingOrder(o));
				// Do nothing
				listeners.stream().forEach(l -> l.afterIssuingOrder(o));
			});
		}
	}
}
