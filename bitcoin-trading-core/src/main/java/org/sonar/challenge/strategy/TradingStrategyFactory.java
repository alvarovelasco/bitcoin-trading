package org.sonar.challenge.strategy;

public interface TradingStrategyFactory<S extends TradingStrategy,T> {
	S getStrategy(T... tr);
}
