package org.sonar.challenge.strategy;

public interface TickCounter {

	/**
	 * @param newTick
	 * @return true if the count of the newTick is below the limit. false if the limit was already reached
	 */
	default boolean countAndCheckCounterUnderLimit(Ticks newTick) {
		return true;
	}
	
}
