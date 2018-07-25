package org.sonar.challenge.trade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.sonar.challenge.rest.BitsoTradesRESTRequest;

public class TradingEngineBuilderImpl implements TradingEngineBuilder {

	private int limit;

	private String bookName;

	public TradingEngineBuilderImpl() {
	}

	public TradingEngineBuilderImpl limit(int limit) {
		this.limit = limit;
		return this;
	}

	public TradingEngineBuilderImpl bookName(String bookName) {
		this.bookName = bookName;
		return this;
	}

	@Override
	public TradingEngine build(Optional<TradingEngine> baseEngine) {
		TradingEngine newTradingEngine = new TradingEngine(
				new BitsoTradesRESTRequest(Objects.requireNonNull(bookName), limit), limit);
		
		if (baseEngine.isPresent()) {
			List<TradingEngineListener> oldListeners = baseEngine.get().getListeners();
			oldListeners.stream().forEach(l -> newTradingEngine.addListener(l));
			newTradingEngine.snapshotTradeList(baseEngine.get());
		}

		return newTradingEngine;
	}

}
