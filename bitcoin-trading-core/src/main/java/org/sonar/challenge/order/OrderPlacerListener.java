package org.sonar.challenge.order;

public interface OrderPlacerListener {

	void beforePlacingOrder(Order o);

	void afterPlacingOrder(Order o);
	
}
