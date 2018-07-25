package org.sonar.challenge.trade;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class TradingEngineManager {

	// Executor instance
	private TradingEngineExecutor<?> defaultEngineExecutor = 
			new TradingEngineSchedulingExecutorImpl(Duration.ofSeconds(20));
	
	// Map of engines and executions
	private final Map<Object, ExecutorService> mapEnginesExecutions = new HashMap<>();
	
	
	public <T extends TradingEngine<?>,E extends ExecutorService>
		void execute(T t) {
		execute(t, defaultEngineExecutor);
	}
	
	public <T extends TradingEngine<?>,E extends ExecutorService> 
			void execute(T t, TradingEngineExecutor<E> executor) {
		Object key = t.getKey();
		if (mapEnginesExecutions.containsKey(key)) {
			mapEnginesExecutions.get(key).shutdown();
		}
		
		ExecutorService executorService = executor.execute(t);
		mapEnginesExecutions.put(key, executorService);
	}
	
}
