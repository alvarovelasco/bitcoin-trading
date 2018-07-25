package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.exception.BitsoSubscribeException;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.util.GSonBuilder;
import org.sonar.challenge.websocket.BitsoSubscriber;
import org.sonar.challenge.websocket.BitsoSubscriber.Handler;
import org.sonar.challenge.websocket.SubscriptionTypes;

/**
 * 
 * @author Alvaro
 *
 */
public class SubscribeDiffOrderCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;

	private final BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber;

	private final ExecutorService executorService;

	protected SubscribeDiffOrderCommandExecutor(BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber,
			CreateDiffOrderForBookUpdateCommandContext context, ExecutorService executorService) {
		this.context = requireNonNull(context);
		this.bitsoSubscriber = requireNonNull(bitsoSubscriber);
		this.executorService = requireNonNull(executorService);
	}

	@Override
	public void execute() throws SonarChallengeException {
		context.setBitsoSubscriber(bitsoSubscriber);

		try {
			executorService.submit(() -> {
				try {					
					bitsoSubscriber.subscribe();
				} catch (BitsoSubscribeException e) {
					throw new RuntimeException(e);
				}
			}).get();
		} catch (ExecutionException | InterruptedException | RuntimeException e) {
			throw new BitsoSubscribeException(e);
		}
	}

	public static class Builder {
		private CreateDiffOrderForBookUpdateCommandContext context;

		private String book;

		private Handler<DiffOrderDecoder> handler;

		private Optional<ExecutorService> executorService = Optional.empty();

		public Builder context(CreateDiffOrderForBookUpdateCommandContext context) {
			this.context = context;
			return this;
		}

		public Builder book(String book) {
			this.book = book;
			return this;
		}

		public Builder handler(Handler<DiffOrderDecoder> handler) {
			this.handler = handler;
			return this;
		}

		public Builder executorService(ExecutorService executorService) {
			this.executorService = Optional.ofNullable(executorService);
			return this;
		}

		public SubscribeDiffOrderCommandExecutor build() {
			BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber = new BitsoSubscriber<>(requireNonNull(book),
					SubscriptionTypes.DIFF_ORDERS,
					m -> GSonBuilder.buildStandardGson().fromJson(m, DiffOrderDecoder.class));
			bitsoSubscriber.setHandler(handler);
			
			return new SubscribeDiffOrderCommandExecutor(
					bitsoSubscriber,
					 requireNonNull(context),
					executorService.orElse(Executors.newCachedThreadPool()));
		}
	}

}
