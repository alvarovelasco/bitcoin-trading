package org.sonar.challenge.trade;

import java.util.concurrent.ExecutorService;

public interface TradingEngineExecutor<E extends ExecutorService> {
	
	 E execute(TradingEngine<?> tradingEngine);
	 
}
