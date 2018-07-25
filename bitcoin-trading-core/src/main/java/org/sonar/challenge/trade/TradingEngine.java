package org.sonar.challenge.trade;

public interface TradingEngine<T extends TradingEngine<?>> extends Runnable {

	Object getKey();	
	
	void snapshotTradeList(T e);
}
