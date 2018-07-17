package org.sonar.challenge.book.net;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.websocket.BitsoSubscriber;
import org.sonar.challenge.websocket.SubscriptionTypes;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;
import org.sonar.challenge.websocket.DiffOrderMessageCoder;

/**
 * 
 * @author Alvaro
 *
 */
public final class SubscribeDiffOrderCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;

	private final String bookName;

	private final Handler<DiffOrderDecoder> handler;

	// TODO: Executor must be provided from outside
	public SubscribeDiffOrderCommandExecutor(String bookName, Handler<DiffOrderDecoder> handler,
			CreateDiffOrderForBookUpdateCommandContext context) {
		this.context = requireNonNull(context);
		this.bookName = requireNonNull(bookName);
		this.handler = requireNonNull(handler);
	}

	@Override
	public void execute() {
		BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber = new BitsoSubscriber<>(bookName, 
				SubscriptionTypes.DIFF_ORDERS, new DiffOrderMessageCoder());
		bitsoSubscriber.setHandler(handler);
		context.setBitsoSubscriber(bitsoSubscriber);

		Executors.newCachedThreadPool().execute(() -> {
			bitsoSubscriber.subscribe();
			while (true) {
			}
		});
	}

}
