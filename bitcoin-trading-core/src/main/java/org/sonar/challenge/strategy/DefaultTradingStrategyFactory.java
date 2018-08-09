package org.sonar.challenge.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.challenge.book.Trade;

public class DefaultTradingStrategyFactory implements TradingStrategyFactory<Trade>{

	private final TickCounter tickCounter;
	
	public DefaultTradingStrategyFactory(TickCounter tickCounter) {
		this.tickCounter = tickCounter;
	}
	
	
	public DefaultTradingStrategy getStrategy(Trade... trades) {
		List<Trade> tradesList = new ArrayList<>();
		if (trades != null && trades.length > 0) {
			tradesList = Arrays.asList(trades);
		} 
		
		// this seems a bit stupid because there is only one strategy type.
		return new DefaultTradingStrategy(tradesList, 
						new TradeTickResolverImpl(),
						tickCounter);
	}
	
}
