package org.sonar.challenge.book;

public interface Subscription<P> {

	void onUpdate(P p);
	
}
