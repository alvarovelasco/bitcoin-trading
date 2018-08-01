package org.sonar.challenge.order;

public interface OrderIssuerListener {

	void beforeIssuingOrder(Order o);

	void afterIssuingOrder(Order o);
	
}
