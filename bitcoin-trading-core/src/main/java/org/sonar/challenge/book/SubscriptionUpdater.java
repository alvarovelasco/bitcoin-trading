package org.sonar.challenge.book;

public interface SubscriptionUpdater<P> {

	void onUpdate(P p);
	
}
