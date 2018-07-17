package org.sonar.challenge.book;

public interface SubscribeFeeder<P> {

	<S extends SubscriptionUpdater<P>> void subscribe(S s);
	
	void startFeeding();
}
