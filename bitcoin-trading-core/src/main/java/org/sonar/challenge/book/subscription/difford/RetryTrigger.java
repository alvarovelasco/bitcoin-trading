package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *	Class implementing logic to schedule a execution after some seconds in order to approach a retrial process.
 *  The reason for widening the space period between new try-outs is to prevent overloading the system with 
 *  a huge number of threads for running retry. 
 *  
 * @author Alvaro
 */
public class RetryTrigger {
	private int startupTrial = 1;
	
	private final Runnable r ;
	
	protected RetryTrigger(Runnable runnable) {
		this.r = requireNonNull(runnable);
	}

	public void shoot() {
		int seconds = 10 * startupTrial;

		Executors.newSingleThreadScheduledExecutor().schedule(r, seconds, TimeUnit.SECONDS);
		startupTrial = ++startupTrial % 4;
	}
	
		
}
