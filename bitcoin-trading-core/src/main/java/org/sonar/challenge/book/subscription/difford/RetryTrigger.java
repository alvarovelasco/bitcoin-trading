package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RetryTrigger {
	private int startupTrial = 1;
	
	private final Runnable r ;
	
	RetryTrigger(Runnable runnable) {
		this.r = requireNonNull(runnable);
	}

	public void shoot() {
		int seconds = 10 * startupTrial;

		Executors.newSingleThreadScheduledExecutor().schedule(r, seconds, TimeUnit.SECONDS);
		startupTrial = ++startupTrial % 4;
	}
	
		
}
