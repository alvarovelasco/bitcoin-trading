package org.sonar.challenge.book.json;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder.PayloadOrder;
import org.sonar.challenge.order.Order;
import org.sonar.challenge.order.OrderType;

/**
 * 
 * @author Alvaro
 *
 */
public class TransformerFactory {

	private static final TransformerFactory INSTANCE = new TransformerFactory();

	private TransformerFactory() {
	}

	public static TransformerFactory getInstance() {
		return INSTANCE;
	}

	public Transformer<OrderBookDecoder, OrderBook> getOrderBookDecoderTransformer(final String bookName) {
		return new Transformer<OrderBookDecoder, OrderBook>() {

			private final Function<OrderBookDecoder.Order, Order> getTransformer(OrderType type,
					LocalDateTime time) {
				return o -> new Order(o.getPrice(), o.getAmount(), type, time);
			}

			public OrderBook transform(OrderBookDecoder origin) {
				long sequence = Long.parseLong(origin.getSequence());

				OrderBook orderBook = new OrderBook(bookName, sequence);

				origin.getAsks().stream().filter(Objects::nonNull).map(getTransformer(OrderType.SELL, origin.getUpdateTime()))
						.forEach(o -> orderBook.addAsk(o));
				origin.getBids().stream().filter(Objects::nonNull).map(getTransformer(OrderType.BUY, origin.getUpdateTime()))
						.forEach(o -> orderBook.addBid(o));

				return orderBook;
			}
		};
	}

	public Transformer<DiffOrderDecoder, OrderBook> getDiffOrderDecoderTransformer() {
		return new Transformer<DiffOrderDecoder, OrderBook>() {

			private final Function<PayloadOrder, Order> transformerFunction = po -> new Order(
					po.getValue().divide(po.getAmount(), 2, RoundingMode.HALF_UP),
					po.getAmount(),
					tranformToOrderType.transform(po.getType()),
					Instant.ofEpochMilli(po.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime());

			@Override
			public OrderBook transform(DiffOrderDecoder origin) {
				OrderBook orderBook = new OrderBook(origin.getBook(), origin.getSequence());

				List<PayloadOrder> filteredPayloadOrders =
						origin.getPayload().stream().
							filter(Objects::nonNull).
							filter(po -> Objects.equals(DiffOrderState.OPEN, po.getState())).
						collect(Collectors.toList());
				
				filteredPayloadOrders.stream().
						filter(po -> DiffOrderMessageType.BUY.getNumber() == po.getType()).
							map(transformerFunction).
							forEach(o -> orderBook.addAsk(o));
				
				filteredPayloadOrders.stream().
						filter(po -> DiffOrderMessageType.SELL.getNumber() == po.getType()).
							map(transformerFunction).
							forEach(o -> orderBook.addBid(o));

				return orderBook;
			}
		};
	}
	
	private Transformer<Integer, OrderType> tranformToOrderType = s -> {
		if (s != 1 && s != 0) 
			throw new IllegalArgumentException("Unexpected type " + s);
		return s == 0 ? OrderType.BUY : 
					 OrderType.SELL; 
	};

	public static interface Transformer<O, R> {
		R transform(O origin);
	}
}
