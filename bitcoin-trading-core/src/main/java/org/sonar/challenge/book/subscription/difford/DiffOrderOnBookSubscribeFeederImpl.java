package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.UpdatedOrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.book.json.TransformerFactory;
import org.sonar.challenge.book.subscription.SubscribeFeeder;
import org.sonar.challenge.book.subscription.SubscriptionUpdater;
import org.sonar.challenge.exception.BitsoSubscribeException;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.websocket.BitsoSubscriber;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;
import org.sonar.challenge.websocket.SubscriptionTypes;

/**
 * 
 * @author Alvaro Velasco
 *
 */
public class DiffOrderOnBookSubscribeFeederImpl implements SubscribeFeeder<UpdatedOrderBook> {

	private static final Logger logger = LoggerFactory.getLogger(DiffOrderOnBookSubscribeFeederImpl.class);

	private final String orderBookName;

	private final List<SubscriptionUpdater<UpdatedOrderBook>> subscriptors = new ArrayList<>();

	private final List<DiffOrderDecoder> messageQueue = new CopyOnWriteArrayList<>();
	
	private final RetryTrigger retryTrigger = new RetryTrigger(() -> startFeeding());

	private CreateDiffOrderForBookUpdateCommandContext context;

	private SubscribeDiffOrderCommandExecutor subscribeDiffOrderCommandExecutor;

	private OrderBookRESTProvideToContextCommandExecutor orderBookRESTProvideToContextCommandExecutor;

	private DiffOrderMergeIntoOrderBookCommandExecutor diffOrderMergeIntoOrderBookCommandExecutor;

	// These two handlers are not covered by tests. Probably they should be abstracted into own classes.
	private final Handler<DiffOrderDecoder> queueMessageHandler = d -> {
		if (!Objects.isNull(d.getBook()) && Objects.equals(SubscriptionTypes.DIFF_ORDERS.getKeyword(), d.getType())) {
			messageQueue.add(d);
		}

		logger.debug(" QUEUEd!");
	};

	private final Handler<DiffOrderDecoder> normalDigestHandler = d -> {
		if (!Objects.equals(SubscriptionTypes.DIFF_ORDERS.getKeyword(), d.getType()) || Objects.isNull(d.getBook())) {
			return;
		}

		OrderBook transformedOrderBook = TransformerFactory.getInstance().getDiffOrderDecoderTransformer().transform(d);

		OrderBook updatedBook = DiffOrderMergeIntoOrderBookCommandExecutor.merge(context.getOrderBook(),
				Arrays.asList(transformedOrderBook));

		UpdatedOrderBook updatedOrderBook = new UpdatedOrderBook(updatedBook, transformedOrderBook.getBids(),
				transformedOrderBook.getAsks());

		logger.debug(" Updated order book {} ", updatedOrderBook);
		if (!updatedOrderBook.getNewAsks().isEmpty() || !updatedOrderBook.getNewBids().isEmpty()) {
			notifySubscriptors(updatedOrderBook);
		}
	};

	public DiffOrderOnBookSubscribeFeederImpl(String bookName) {
		this.orderBookName = requireNonNull(bookName);
		defaultInitDependencies();
	}

	protected DiffOrderOnBookSubscribeFeederImpl(String bookName, CreateDiffOrderForBookUpdateCommandContext context,
			SubscribeDiffOrderCommandExecutor subscribeDiffOrderCommandExecutor,
			OrderBookRESTProvideToContextCommandExecutor orderBookRESTProvideToContextCommandExecutor,
			DiffOrderMergeIntoOrderBookCommandExecutor diffOrderMergeIntoOrderBookCommandExecutor) {
		this.orderBookName = requireNonNull(bookName);
		initDependencies(context, subscribeDiffOrderCommandExecutor, orderBookRESTProvideToContextCommandExecutor,
				diffOrderMergeIntoOrderBookCommandExecutor);
	}

	private void defaultInitDependencies() {
		context = new CreateDiffOrderForBookUpdateCommandContext();
		initDependencies(context,
				new SubscribeDiffOrderCommandExecutor.Builder().book(orderBookName).context(context)
						.handler(queueMessageHandler).build(),
				new OrderBookRESTProvideToContextCommandExecutor(context, orderBookName),
				new DiffOrderMergeIntoOrderBookCommandExecutor(context, () -> dequeue()));
	}

	private void initDependencies(CreateDiffOrderForBookUpdateCommandContext context,
			SubscribeDiffOrderCommandExecutor subscribeDiffOrderCommandExecutor,
			OrderBookRESTProvideToContextCommandExecutor orderBookRESTProvideToContextCommandExecutor,
			DiffOrderMergeIntoOrderBookCommandExecutor diffOrderMergeIntoOrderBookCommandExecutor) {
		this.context = context;
		this.subscribeDiffOrderCommandExecutor = subscribeDiffOrderCommandExecutor;
		this.orderBookRESTProvideToContextCommandExecutor = orderBookRESTProvideToContextCommandExecutor;
		this.diffOrderMergeIntoOrderBookCommandExecutor = diffOrderMergeIntoOrderBookCommandExecutor;
	}

	@Override
	public <S extends SubscriptionUpdater<UpdatedOrderBook>> void subscribe(S s) {
		subscriptors.add(s);
	}

	/**
	 * Start the feeding process.
	 */
	@Override
	public void startFeeding() {
		try {
			subscribeDiffOrderCommandExecutor.execute();

			orderBookRESTProvideToContextCommandExecutor.execute();

			synchronized (messageQueue) {
				diffOrderMergeIntoOrderBookCommandExecutor.execute();
				context.getBitsoSubscriber().setHandler(normalDigestHandler);
			}
			if (context.getOrderBook() != null) {
				UpdatedOrderBook firstOrderBook = new UpdatedOrderBook(context.getOrderBook(), new ArrayList<>(),
						new ArrayList<>());
				notifySubscriptors(firstOrderBook);
			}
		} catch (SonarChallengeException e) {
			e.printStackTrace();
			retryTrigger.shoot();
		}
	}

	/**
	 * Stops the feeding process. Shutdown of any connection to the bitso endpoint
	 */
	@Override
	public void stopFeeding() {
		try {
			if (context.getBitsoSubscriber() != null) {
				BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber = context.getBitsoSubscriber();
				bitsoSubscriber.close();
			}
		} catch (BitsoSubscribeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dequeue the internal list of messages received by the bitso subscriber. 
	 * This will leave the list clean and empty.
	 * @return
	 */
	protected List<DiffOrderDecoder> dequeue() {
		List<DiffOrderDecoder> diffOrderDecoders = new ArrayList<>(messageQueue);
		messageQueue.clear();
		return diffOrderDecoders;
	}

	protected void notifySubscriptors(UpdatedOrderBook updatedOrderBook) {
		subscriptors.stream().forEach(s -> s.onUpdate(updatedOrderBook));
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SubscriptionUpdater<UpdatedOrderBook> subscription = p -> System.out.println(p.toString());

		DiffOrderOnBookSubscribeFeederImpl feederImpl = new DiffOrderOnBookSubscribeFeederImpl("btc_mxn");
		feederImpl.subscribe(subscription);
		feederImpl.startFeeding();
	}

}
