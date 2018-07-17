package org.sonar.challenge.book;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.sonar.challenge.book.net.CreateDiffOrderForBookUpdateCommandContext;
import org.sonar.challenge.book.net.DiffOrderMergeIntoOrderBookCommandExecutor;
import org.sonar.challenge.book.net.OrderBookRESTProvideToContextCommandExecutor;
import org.sonar.challenge.book.net.SubscribeDiffOrderCommandExecutor;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.book.net.json.TransformerFactory;
import org.sonar.challenge.rest.BitsoOrderBookRESTRequest;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;

import com.google.gson.Gson;

public class OrderBookSubscribeFeederImpl implements SubscribeFeeder<UpdatedOrderBook> {

	private final String orderBookName;

	private final List<Subscription<UpdatedOrderBook>> subscriptors = new ArrayList<>();

	private final CreateDiffOrderForBookUpdateCommandContext context = new CreateDiffOrderForBookUpdateCommandContext();

	private List<String> messageQueue = new CopyOnWriteArrayList<>();
	private final Handler queueMessageHandler = m -> {
		messageQueue.add(m);
	};

	private final Handler normalDigestHandler = m -> {
		DiffOrderDecoder decodedDiffOrder = new Gson().fromJson(m, DiffOrderDecoder.class);
		OrderBook transformedOrderBook = TransformerFactory.getInstance().getDiffOrderDecoderTransformer()
				.transform(decodedDiffOrder);

		OrderBook updatedBook = DiffOrderMergeIntoOrderBookCommandExecutor.merge(context.getOrderBook(),
				Arrays.asList(transformedOrderBook));

		UpdatedOrderBook updatedOrderBook = new UpdatedOrderBook(updatedBook, transformedOrderBook.getBids(),
				transformedOrderBook.getAsks());
		notifySubscriptors(updatedOrderBook);
	};

	public OrderBookSubscribeFeederImpl(String bookName) {
		this.orderBookName = requireNonNull(bookName);
	}

	public <S extends Subscription<UpdatedOrderBook>> void subscribe(S s) {
		subscriptors.add(s);
	}

	public void startFeeding() {
		{
			new SubscribeDiffOrderCommandExecutor(orderBookName, queueMessageHandler, context).execute();

			new OrderBookRESTProvideToContextCommandExecutor(context,
					TransformerFactory.getInstance().getOrderBookDecordeTransformer(orderBookName),
					new BitsoOrderBookRESTRequest(orderBookName)).execute();

			new DiffOrderMergeIntoOrderBookCommandExecutor(context, dequeue()).execute();
			context.getBitsoSubscriber().setHandler(normalDigestHandler);
			// If some message was queued during the first merge, it's merged again
			new DiffOrderMergeIntoOrderBookCommandExecutor(context, dequeue()).execute();
		}
		
		UpdatedOrderBook firstOrderBook = new UpdatedOrderBook(context.getOrderBook(), new ArrayList<>(),
				new ArrayList<>());
		notifySubscriptors(firstOrderBook);
	}

	// TODO Extract into another class
	private List<DiffOrderDecoder> dequeue() {
		List<DiffOrderDecoder> diffOrderDecoders = messageQueue.stream()
				.map(m -> new Gson().fromJson(m, DiffOrderDecoder.class)).collect(Collectors.toList());
		messageQueue.clear();
		return diffOrderDecoders;
	}

	private void notifySubscriptors(UpdatedOrderBook updatedOrderBook) {
		subscriptors.stream().forEach(s -> s.onUpdate(updatedOrderBook));
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Subscription<UpdatedOrderBook> subscription = p -> System.out.println(p.toString());

		OrderBookSubscribeFeederImpl feederImpl = new OrderBookSubscribeFeederImpl("btc_mxn");
		feederImpl.subscribe(subscription);
		Executors.newCachedThreadPool().submit(() -> {
			feederImpl.startFeeding();
			while (true) {
			}
		}).get();
	}

}
