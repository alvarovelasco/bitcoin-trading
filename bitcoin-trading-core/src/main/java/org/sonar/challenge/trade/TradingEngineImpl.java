package org.sonar.challenge.trade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.sonar.challenge.book.Trade;
import org.sonar.challenge.book.json.TradeResultDecoder;
import org.sonar.challenge.order.Order;
import org.sonar.challenge.order.OrderBatch;
import org.sonar.challenge.order.OrderIssuer;
import org.sonar.challenge.order.OrderIssuerListener;
import org.sonar.challenge.rest.BitsoTradesRESTRequest;
import org.sonar.challenge.strategy.TradingStrategy;
import org.sonar.challenge.strategy.TradingStrategyFactory;
import org.sonar.challenge.util.GSonBuilder;

/**
 * Implementation of TradingEngine for implementing the default behaviour of the engine expected to fetch 
 * trades, possibly apply strategies based on these trades, issuing the orders resulting from 
 * applying the strategies, and notifying the listeners with the resulting trades.
 * 
 * 
 * @author Alvaro Velasco
 *
 */
//TODO AVF: Unit testing
public class TradingEngineImpl implements TradingEngine<TradingEngineImpl> {

	private final List<Trade> tradeList = new ArrayList<>();

	private final BitsoTradesRESTRequest tradesRequest;
	
	private final List<TradingEngineListener> listeners = new ArrayList<>();
	
	private final OrderIssuerToTradeList issuerToTradeList = new OrderIssuerToTradeList();
	
	private final List<TradingStrategyFactory> strategiesToApply = new ArrayList<>();
	
	private final List<OrderIssuer> orderIssuers = new ArrayList<>();
	
	private final int limit;

	TradingEngineImpl(BitsoTradesRESTRequest tradesRequest, int limit) {
		this.tradesRequest = Objects.requireNonNull(tradesRequest);
		this.limit = limit;
	}
	
	public void addListener(TradingEngineListener listener) {
		listeners.add(listener);
	}
	
	List<TradingEngineListener> getListeners() {
		return new ArrayList<>(listeners);
	}
	
	public void addOrderIssuer(OrderIssuer orderIssuer) {
		this.orderIssuers.add(orderIssuer);
		orderIssuer.addListener(issuerToTradeList);
	}
	
	public void addTradingStrategyFactory(TradingStrategyFactory tradingStrategyFactory) {
		this.strategiesToApply.add(tradingStrategyFactory);
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


			final Trade[] tradeArr = tradeList.toArray(new Trade[tradeList.size()]);
			// From the current trade list apply strategy and collect new orders
			List<TradingStrategy> tradingStrategies = 
					strategiesToApply.stream().map(f -> f.getStrategy(tradeArr)).collect(Collectors.toList());

			List<Order> newOrders = tradingStrategies.stream().
				map(s -> s.resolveOrders()).
				map(b -> b.getNewOrdersToPlace()).
				flatMap(Collection::stream).
				collect(Collectors.toList());
			final OrderBatch batch = new OrderBatch(newOrders);
			
			// New orders must be issued. Attention on synchronous call for collecting brand new trades
			orderIssuers.stream().forEach(oi -> oi.issue(batch));

			// Add brand new trades to tradeList			
			List<Trade> newTrades = issuerToTradeList.dequeueTrades();			
			newTrades.stream().forEach(t -> tradeList.add(0, t));
			while (tradeList.size() > limit) {
				tradeList.remove(limit);
			}
			
			// Notify listeners
			listeners.stream().forEach(l -> l.onTradeListChange(oldTradeList, tradeList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void snapshotTradeList(TradingEngineImpl e) {
		this.tradeList.clear();
		this.tradeList.addAll(e.tradeList);
	}

	@Override
	public Object getKey() {
		return tradesRequest;
	}
	
	static class OrderIssuerToTradeList implements OrderIssuerListener {

		private final List<Trade> trades = new ArrayList<>();
		
		@Override
		public void beforeIssuingOrder(Order o) {
		}

		@Override
		public void afterIssuingOrder(Order o) {
			trades.add(new Trade.TradeBuilder().
					amount(o.getAmount()).
					price(o.getPrice()).
					timestamp(o.getTimestamp()).
				build());
		}
		
		public List<Trade> dequeueTrades() {
			List<Trade> newTradesList = new ArrayList<>(trades);
			trades.clear();
			
			return newTradesList;
		}
		
	}
}
