package org.sonar.challenge.book.subscription;

public interface SubscriptionUpdater<P> {

	void onUpdate(P p);
	
}
