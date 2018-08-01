package org.sonar.challenge.order;

public interface OrderIssuer {

	void addListener(OrderIssuerListener listener);
	
	void issue(OrderBatch orderBatch);
	
}
