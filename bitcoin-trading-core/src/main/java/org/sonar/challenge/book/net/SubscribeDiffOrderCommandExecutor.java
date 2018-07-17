package org.sonar.challenge.book.net;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.sonar.challenge.websocket.BitsoSubscriber;
import org.sonar.challenge.websocket.SubscriptionTypes;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;

/**
 * 
 * @author Alvaro
 *
 */
public final class SubscribeDiffOrderCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;

	private final String bookName;

	private final Handler handler;

	// TODO: Executor must be provided from outside
	public SubscribeDiffOrderCommandExecutor(String bookName, Handler handler,
			CreateDiffOrderForBookUpdateCommandContext context) {
		this.context = requireNonNull(context);
		this.bookName = requireNonNull(bookName);
		this.handler = requireNonNull(handler);
	}

	@Override
	public void execute() {
		BitsoSubscriber bitsoSubscriber = new BitsoSubscriber(bookName, SubscriptionTypes.DIFF_ORDERS);
		bitsoSubscriber.setHandler(handler);
		context.setBitsoSubscriber(bitsoSubscriber);

		Executors.newCachedThreadPool().execute(() -> {
			bitsoSubscriber.subscribe();
			while (true) {
			}
		});
	}

}
