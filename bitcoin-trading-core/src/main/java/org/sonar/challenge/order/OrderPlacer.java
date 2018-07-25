package org.sonar.challenge.order;

public interface OrderPlacer {

	void addListener(OrderPlacerListener listener);
	
	void place(OrderBatch orderBatch);
	
}
