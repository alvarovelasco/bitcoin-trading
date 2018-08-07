package org.sonar.challenge.trade;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.sonar.challenge.rest.BitsoTradesRESTRequest;

/**
 * 
 * Executes a trading engine runnable instance at a new {@link ScheduledExecutorService} whose delay is 
 * set up at the constructor
 * 
 * @author Alvaro Velasco
 *
 */
public final class TradingEngineSchedulingExecutorImpl implements TradingEngineExecutor<ScheduledExecutorService> {

	private final Duration duration;

	/**
	 * @param duration Implicit delay on the scheduled executor of trading engine
	 */
	TradingEngineSchedulingExecutorImpl(Duration duration) {
		this.duration = duration;
	}

	@Override
	public ScheduledExecutorService execute(TradingEngine<?> tradingEngine) {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
		scheduledExecutorService.scheduleAtFixedRate(tradingEngine, 0, duration.getSeconds(), TimeUnit.SECONDS);
		return scheduledExecutorService;
	}

	public static void main(String[] args) {
		TradingEngineExecutor<? extends ExecutorService> engineScheduler = new TradingEngineSchedulingExecutorImpl(
				Duration.ofSeconds(10));
		TradingEngineImpl te = new TradingEngineImpl(new BitsoTradesRESTRequest("btc_mxn", 25), 25);
		te.addListener((oldL, newL) -> System.out.println(oldL + " \n " + newL));
		engineScheduler.execute(te);
	}
}
