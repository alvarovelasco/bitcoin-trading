package org.sonar.challenge.strategy;

public interface TradingStrategyConfigurationBuilder<S extends TradingStrategy> {

	S build();
	
}
