package org.sonar.challenge.trade;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.sonar.challenge.rest.BitsoTradesRESTRequest;

public class TradingEngineSchedulerImpl implements TradingEngineScheduler {

	TradingEngineSchedulerImpl() {
	}
	
	@Override
	public ScheduledExecutorService schedule(TradingEngine tradingEngine, Duration duration) {
		ScheduledExecutorService scheduledExecutorService = 
				Executors.newScheduledThreadPool(10);
		scheduledExecutorService
				.scheduleAtFixedRate(tradingEngine, 0, duration.getSeconds(), TimeUnit.SECONDS);
		return scheduledExecutorService;
	}

	public static void main(String[] args) {
		TradingEngineScheduler engineScheduler = new  TradingEngineSchedulerImpl();
		TradingEngine te = new TradingEngine(new BitsoTradesRESTRequest("btc_mxn", 25), 25);
		te.addListener( (oldL,newL) -> System.out.println(oldL + " \n " +newL));
		engineScheduler.schedule(te, Duration.ofSeconds(10));
	}
}
