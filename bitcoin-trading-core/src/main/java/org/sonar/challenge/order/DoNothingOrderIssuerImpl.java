package org.sonar.challenge.order;

import java.util.ArrayList;
import java.util.List;
//TODO AVF: Unit testing
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
				System.out.println("ISSUEING FOR " + o);
				listeners.stream().forEach(l -> l.afterIssuingOrder(o));
			});
		}
	}
}
