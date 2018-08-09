package org.sonar.challenge.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TickCounterImpl implements TickCounter {
	
	private final int downticksLimit;	
	
	private final int upticksLimit;
	
	private Map<Ticks, Integer> counter = new HashMap<>();
	
	public TickCounterImpl(int downticksLimit, int upticksLimit) {
		this.downticksLimit = downticksLimit;
		this.upticksLimit = upticksLimit;
	}
	
	@Override
	public boolean countAndCheckCounterUnderLimit(Ticks newTick) {
		int countValue = 0;
		if (counter.containsKey(newTick)) { 
			countValue = counter.get(newTick);
		}
		
		countValue++;
		
		counter.put(newTick, countValue);
		
		return getLimit(newTick) > countValue;
	}
	

	private int getLimit(Ticks tick) {
		Objects.requireNonNull(tick);
		if (Ticks.UPTICK.equals(tick)) {
			return upticksLimit;
		} else {
			return downticksLimit;
		}
	}
}
