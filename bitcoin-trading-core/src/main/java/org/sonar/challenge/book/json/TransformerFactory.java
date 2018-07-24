package org.sonar.challenge.book.json;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Function;

import org.sonar.challenge.book.Order;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder.PayloadOrder;

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

			private final Function<org.sonar.challenge.book.json.OrderBookDecoder.Order, Order> getTransformer(
					LocalDateTime time) {
				return o -> new Order(o.getPrice(), o.getAmount(), time);
			}

			public OrderBook transform(OrderBookDecoder origin) {
				long sequence = Long.parseLong(origin.getSequence());

				OrderBook orderBook = new OrderBook(bookName, sequence);

				origin.getAsks().stream().filter(Objects::nonNull).map(getTransformer(origin.getUpdateTime()))
						.forEach(o -> orderBook.addAsk(o));
				origin.getBids().stream().filter(Objects::nonNull).map(getTransformer(origin.getUpdateTime()))
						.forEach(o -> orderBook.addBid(o));

				return orderBook;
			}
		};
	}

	public Transformer<DiffOrderDecoder, OrderBook> getDiffOrderDecoderTransformer() {
		return new Transformer<DiffOrderDecoder, OrderBook>() {

			private final Function<PayloadOrder, Order> transformerFunction = po -> new Order(po.getValue(),
					po.getAmount(),
					Instant.ofEpochMilli(po.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime());

			@Override
			public OrderBook transform(DiffOrderDecoder origin) {
				OrderBook orderBook = new OrderBook(origin.getBook(), origin.getSequence());

				origin.getPayload().stream().filter(Objects::nonNull)
						.filter(po -> DiffOrderMessageType.BUY.getNumber() == po.getType())
						.filter(po -> !Objects.equals(DiffOrderState.CANCELLED, po.getState())).map(transformerFunction)
						.forEach(o -> orderBook.addAsk(o));
				origin.getPayload().stream().filter(Objects::nonNull)
						.filter(po -> DiffOrderMessageType.SELL.getNumber() == po.getType())
						.filter(po -> !Objects.equals(DiffOrderState.CANCELLED, po.getState())).map(transformerFunction)
						.forEach(o -> orderBook.addBid(o));

				return orderBook;
			}
		};
	}

	public static interface Transformer<O, R> {
		R transform(O origin);
	}
}
