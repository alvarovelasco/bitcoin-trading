package org.sonar.challenge.trade;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.sonar.challenge.rest.BitsoTradesRESTRequest;

public class TradingEngineImplBuilder implements TradingEngineBuilder<TradingEngineImpl> {

	private int limit = 10;

	private String bookName = null;
	
	private Optional<TradingEngineImpl> baseEngine = Optional.empty();

	public TradingEngineImplBuilder() {
	}

	public TradingEngineImplBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	public TradingEngineImplBuilder bookName(String bookName) {
		this.bookName = bookName;
		return this;
	}
	
	public TradingEngineImplBuilder inherateStateFrom(TradingEngineImpl tradingEngineImpl) {
		this.baseEngine = Optional.ofNullable(tradingEngineImpl);
		return this;
	}

	@Override
	public TradingEngineImpl build() {
		TradingEngineImpl newTradingEngine = new TradingEngineImpl(
				new BitsoTradesRESTRequest(Objects.requireNonNull(bookName), limit), limit);
		
		if (baseEngine.isPresent()) {
			List<TradingEngineListener> oldListeners = baseEngine.get().getListeners();
			oldListeners.stream().forEach(l -> newTradingEngine.addListener(l));
			newTradingEngine.snapshotTradeList(baseEngine.get());
		}

		return newTradingEngine;
	}

}
