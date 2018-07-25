package org.sonar.challenge.book.subscription;

public interface SubscribeFeeder<P> {

	<S extends SubscriptionUpdater<P>> void subscribe(S s);
	
	void startFeeding();
	
	void stopFeeding();
}
