package org.sonar.challenge.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.sonar.challenge.book.Trade;
import org.sonar.challenge.book.json.TradeResultDecoder;
import org.sonar.challenge.rest.BitsoTradesRESTRequest;
import org.sonar.challenge.util.GSonBuilder;

public class TradingEngine implements Runnable {

	private final List<Trade> tradeList = new ArrayList<>();

	private final BitsoTradesRESTRequest tradesRequest;
	
	private final List<TradingEngineListener> listeners = new ArrayList<>();
	
	private final int limit;

	TradingEngine(BitsoTradesRESTRequest tradesRequest, int limit) {
		this.tradesRequest = Objects.requireNonNull(tradesRequest);
		this.limit = limit;
	}
	
	public void addListener(TradingEngineListener listener) {
		listeners.add(listener);
	}
	
	List<TradingEngineListener> getListeners() {
		return new ArrayList<>(listeners);
	}

	@Override
	public void run() {
		try {
			List<Trade> oldTradeList = new ArrayList<>(tradeList);
			
			// Get the new list with trades
			String tradesString = tradesRequest.request();
			TradeResultDecoder tradeResult = GSonBuilder.buildStandardGson().fromJson(tradesString,
					TradeResultDecoder.class);
			List<Trade> freshTradesList = tradeResult.getPayload().
									stream().map(t -> 
										new Trade.TradeBuilder().
												amount(t.getAmount()).
												price(t.getPrice()).
												timestamp(t.getTimestamp()).
												tradeId(t.getTradeId()).
											build()	
									).collect(Collectors.toList());
			
			// Get the new trades from this list.
			List<Trade> onlyOldOnlineTrades = tradeList.stream().filter(t -> t.getTradeId().isPresent())
					.collect(Collectors.toList());
			
			// Only new trades
			freshTradesList.removeAll(onlyOldOnlineTrades);
			
			// Add the fresh new trades to our list
			freshTradesList.stream().forEach(t -> tradeList.add(0, t));
			while (tradeList.size() > limit) {
				tradeList.remove(limit);
			}
			
			// TODO : From the current trade list apply strategy and collect new orders
			
			// TODO : New orders must be issued. Attention on synchronous call for collecting brand new trades
			
			// TODO: Add brand new trades to tradeList

			// Notify listeners
			listeners.stream().forEach(l -> l.onTradeListChange(oldTradeList, tradeList));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void snapshotTradeList(TradingEngine e) {
		this.tradeList.clear();
		this.tradeList.addAll(e.tradeList);
	}
}
