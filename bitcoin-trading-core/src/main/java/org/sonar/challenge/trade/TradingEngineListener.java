package org.sonar.challenge.trade;

import java.util.List;

import org.sonar.challenge.book.Trade;

public interface TradingEngineListener {

	void onTradeListChange(List<Trade> oldList, List<Trade> newTradeList);
	
}
