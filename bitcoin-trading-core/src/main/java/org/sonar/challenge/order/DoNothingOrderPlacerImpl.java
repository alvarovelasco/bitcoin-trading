package org.sonar.challenge.order;

import java.util.ArrayList;
import java.util.List;

public final class DoNothingOrderPlacerImpl implements OrderPlacer {

	private List<OrderPlacerListener> listeners = new ArrayList<>();
	
	@Override
	public void addListener(OrderPlacerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void place(OrderBatch orderBatch) {
		if (orderBatch != null) {
			orderBatch.getNewOrdersToPlace().stream().filter(o -> o != null).forEach(o -> {
				listeners.stream().forEach(l -> l.beforePlacingOrder(o));
				// Do nothing
				listeners.stream().forEach(l -> l.afterPlacingOrder(o));
			});
		}
	}
}
