package org.sonar.challenge.trade;

import java.util.Optional;

public interface TradingEngineBuilder<T extends TradingEngine<?>> {

	T build();	
	
}
