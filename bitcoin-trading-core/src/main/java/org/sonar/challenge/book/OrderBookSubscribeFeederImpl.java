package org.sonar.challenge.book;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.challenge.book.net.CreateDiffOrderForBookUpdateCommandContext;
import org.sonar.challenge.book.net.DiffOrderMergeIntoOrderBookCommandExecutor;
import org.sonar.challenge.book.net.OrderBookRESTProvideToContextCommandExecutor;
import org.sonar.challenge.book.net.SubscribeDiffOrderCommandExecutor;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.book.net.json.TransformerFactory;
import org.sonar.challenge.rest.BitsoOrderBookRESTRequest;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;
import org.sonar.challenge.websocket.SubscriptionTypes;

public class OrderBookSubscribeFeederImpl implements SubscribeFeeder<UpdatedOrderBook> {

	private static final Logger logger = LoggerFactory.getLogger(OrderBookSubscribeFeederImpl.class);
	
	private final String orderBookName;

	private final List<SubscriptionUpdater<UpdatedOrderBook>> subscriptors = new ArrayList<>();

	private final CreateDiffOrderForBookUpdateCommandContext context = new CreateDiffOrderForBookUpdateCommandContext();

	private List<DiffOrderDecoder> messageQueue = new CopyOnWriteArrayList<>();
	
	private final Handler<DiffOrderDecoder> queueMessageHandler = d -> {
		if (!Objects.isNull(d.getBook()) &&
				Objects.equals(SubscriptionTypes.DIFF_ORDERS.getKeyword(), d.getType())) {
			messageQueue.add(d);
		}

		logger.debug(" QUEUEd!");
	};

	private final Handler<DiffOrderDecoder> normalDigestHandler = d -> {
		if (!Objects.equals(SubscriptionTypes.DIFF_ORDERS.getKeyword(), d.getType())
				|| Objects.isNull(d.getBook())) {
			return ;
		}
		
		OrderBook transformedOrderBook = TransformerFactory.getInstance().getDiffOrderDecoderTransformer()
				.transform(d);

		OrderBook updatedBook = DiffOrderMergeIntoOrderBookCommandExecutor.merge(context.getOrderBook(),
				Arrays.asList(transformedOrderBook));

		UpdatedOrderBook updatedOrderBook = new UpdatedOrderBook(updatedBook, transformedOrderBook.getBids(),
				transformedOrderBook.getAsks());
		
		logger.debug(" Updated order book {} ", updatedOrderBook);
		if (!updatedOrderBook.getNewAsks().isEmpty() || !updatedOrderBook.getNewBids().isEmpty()) {
			notifySubscriptors(updatedOrderBook);
		}
	};

	public OrderBookSubscribeFeederImpl(String bookName) {
		this.orderBookName = requireNonNull(bookName);
	}

	public <S extends SubscriptionUpdater<UpdatedOrderBook>> void subscribe(S s) {
		subscriptors.add(s);
	}

	public void startFeeding() {
		{
			// TODO : It must create and provide the executor
			new SubscribeDiffOrderCommandExecutor(orderBookName, queueMessageHandler, context).execute();

			new OrderBookRESTProvideToContextCommandExecutor(context,
					TransformerFactory.getInstance().getOrderBookDecoderTransformer(orderBookName),
					new BitsoOrderBookRESTRequest(orderBookName)).execute();

			synchronized (messageQueue) {
				new DiffOrderMergeIntoOrderBookCommandExecutor(context, dequeue()).execute();
				context.getBitsoSubscriber().setHandler(normalDigestHandler);
			}
		}

		UpdatedOrderBook firstOrderBook = new UpdatedOrderBook(context.getOrderBook(), new ArrayList<>(),
				new ArrayList<>());
		notifySubscriptors(firstOrderBook);
	}

	private List<DiffOrderDecoder> dequeue() {
		List<DiffOrderDecoder> diffOrderDecoders = new ArrayList<>(messageQueue);
		messageQueue.clear();
		return diffOrderDecoders;
	}

	private void notifySubscriptors(UpdatedOrderBook updatedOrderBook) {
		subscriptors.stream().forEach(s -> s.onUpdate(updatedOrderBook));
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SubscriptionUpdater<UpdatedOrderBook> subscription = p -> System.out.println(p.toString());

		OrderBookSubscribeFeederImpl feederImpl = new OrderBookSubscribeFeederImpl("btc_mxn");
		feederImpl.subscribe(subscription);
		feederImpl.startFeeding();
	}

}
