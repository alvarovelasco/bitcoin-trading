package org.sonar.challenge.strategy;

public interface TradingStrategyFactory<T> {
	TradingStrategy getStrategy(T... tr);
}
