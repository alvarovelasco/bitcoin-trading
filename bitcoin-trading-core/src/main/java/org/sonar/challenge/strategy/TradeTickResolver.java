package org.sonar.challenge.strategy;

import java.util.Optional;

import org.sonar.challenge.book.Trade;

public interface TradeTickResolver {
	Optional<Ticks> resolve(Trade c1, Trade c2);
}
