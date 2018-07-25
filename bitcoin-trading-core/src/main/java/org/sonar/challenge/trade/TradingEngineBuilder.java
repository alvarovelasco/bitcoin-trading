package org.sonar.challenge.trade;

import java.util.Optional;

public interface TradingEngineBuilder {

	TradingEngine  build(Optional<TradingEngine> baseEngine);	
	
}
