package org.sonar.challenge.trade;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;

public interface TradingEngineScheduler {
	ScheduledExecutorService schedule(TradingEngine tradingEngine, Duration duration);
}
