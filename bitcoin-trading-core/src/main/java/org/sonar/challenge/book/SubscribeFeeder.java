package org.sonar.challenge.book;

public interface SubscribeFeeder<P> {

	<S extends Subscription<P>> void subscribe(S s);
	
	void startFeeding();
}
