package org.sonar.challenge.book.net;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.book.net.json.TransformerFactory;

/**
 * 
 * @author Alvaro
 *
 */
public final class DiffOrderMergeIntoOrderBookCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;

	private final List<DiffOrderDecoder> diffOrderList;

	public DiffOrderMergeIntoOrderBookCommandExecutor(CreateDiffOrderForBookUpdateCommandContext context,
			List<DiffOrderDecoder> diffOrderList) {
		this.context = requireNonNull(context);
		this.diffOrderList = requireNonNull(diffOrderList);
	}

	@Override
	public void execute() {
		final OrderBook orderBook = context.getOrderBook();

		if (orderBook == null) {
			// FIXME AVF Change this exception
			throw new RuntimeException("Order book not found");
		}

		if (diffOrderList.isEmpty()) {
			return;
		}
		
		List<DiffOrderDecoder> resolvedDiffOrderList = diffOrderList.stream().filter(Objects::nonNull).
				filter(dod ->  dod.getSequence() > orderBook.getLastSequenceEntered())
				.sorted(Comparator.comparingLong(DiffOrderDecoder::getSequence)).collect(Collectors.toList());

		List<OrderBook> resolvedDiffOrderBooks = resolvedDiffOrderList.stream()
				.map(TransformerFactory.getInstance().getDiffOrderDecoderTransformer()::transform)
				.collect(Collectors.toList());

		OrderBook finalOrderBook = merge(orderBook, resolvedDiffOrderBooks);

		context.setOrderBook(finalOrderBook);
	}

	public static OrderBook merge(OrderBook finalOrderBook, List<OrderBook> resolvedDiffOrderBooks) {
		for (OrderBook rdob : resolvedDiffOrderBooks) {
			OrderBook newOrderBook = new OrderBook(finalOrderBook.getName(), rdob.getLastSequenceEntered());

			finalOrderBook.getAsks().stream().forEach(o -> newOrderBook.addAsk(o));
			rdob.getAsks().stream().forEach(o -> newOrderBook.addAsk(o));
			finalOrderBook.getBids().stream().forEach(o -> newOrderBook.addBid(o));
			rdob.getBids().stream().forEach(o -> newOrderBook.addBid(o));

			finalOrderBook = newOrderBook;
		}
		return finalOrderBook;
	}

}
