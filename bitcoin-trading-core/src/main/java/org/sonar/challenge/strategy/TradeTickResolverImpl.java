package org.sonar.challenge.strategy;

import java.util.Objects;
import java.util.Optional;

import org.sonar.challenge.book.Trade;

public class TradeTickResolverImpl implements TradeTickResolver {

	@Override
	public Optional<Ticks> resolve(Trade c1, Trade c2) {
		Objects.requireNonNull(c1);
		Objects.requireNonNull(c2);
		
		int signum = c1.getPrice().compareTo(c2.getPrice());
		
		if (signum > 0) return Optional.of(Ticks.UPTICK);
		else if (signum < 0) return Optional.of(Ticks.DOWNTICK);
		
		return Optional.empty();
	}

}
