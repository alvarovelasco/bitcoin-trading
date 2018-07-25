package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.book.json.TransformerFactory;
import org.sonar.challenge.exception.OrderBookNotFoundException;
import org.sonar.challenge.exception.SonarChallengeException;

/**
 * 
 * @author Alvaro
 *
 */
public class DiffOrderMergeIntoOrderBookCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;

	private final DiffOrderDecoderListProvider decoderListProvider;

	protected DiffOrderMergeIntoOrderBookCommandExecutor(CreateDiffOrderForBookUpdateCommandContext context,
			DiffOrderDecoderListProvider decoderListProvider) {
		this.context = requireNonNull(context);
		this.decoderListProvider = requireNonNull(decoderListProvider);
	}

	@Override
	public void execute() throws SonarChallengeException {
		final OrderBook orderBook = context.getOrderBook();

		if (orderBook == null) {
			throw new OrderBookNotFoundException("No order book found in the context");
		}

		List<DiffOrderDecoder> diffOrderList = decoderListProvider.provide();
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
	
	public interface DiffOrderDecoderListProvider {
		List<DiffOrderDecoder> provide();
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
