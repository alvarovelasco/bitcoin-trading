package org.sonar.challenge.book.subscription.difford;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.UpdatedOrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.book.subscription.SubscribeFeeder;
import org.sonar.challenge.book.subscription.SubscriptionUpdater;
import org.sonar.challenge.exception.BitsoSubscribeException;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.websocket.BitsoSubscriber;

public class DiffOrderOnBookSubscribeFeederImplTest {

	@Mock
	private SubscribeDiffOrderCommandExecutor subscribeDiffOrderCommandExecutor;

	@Mock
	private DiffOrderMergeIntoOrderBookCommandExecutor mergeIntoOrderBookCommandExecutor;

	@Mock
	private OrderBookRESTProvideToContextCommandExecutor orderBookRESTProvideToContextCommandExecutor;

	@Mock
	private BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber;

	@Mock
	private CreateDiffOrderForBookUpdateCommandContext context;

	private OrderBook initialOrderBook = new OrderBook("whatever", 100);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(context.getBitsoSubscriber()).thenReturn(bitsoSubscriber);
	}

	@Test
	public void test_StartFeeder_whenMockedAllElements() throws SonarChallengeException {
		test_successFeedingStartSerie(0);
	}

	@Test
	public void test_StartFeeder_whenContextInitializedWithInitialOrderBook() throws SonarChallengeException {
		when(context.getOrderBook()).thenReturn(initialOrderBook);
		test_successFeedingStartSerie(1);
	}

	@Test
	public void test_StopFeeder_whenSuccess() throws BitsoSubscribeException {
		SubscribeFeeder<UpdatedOrderBook> diffOrderOnBook = new DiffOrderOnBookSubscribeFeederImpl("whatever", context,
				subscribeDiffOrderCommandExecutor, orderBookRESTProvideToContextCommandExecutor,
				mergeIntoOrderBookCommandExecutor);
		diffOrderOnBook.stopFeeding();

		verify(bitsoSubscriber, VerificationModeFactory.times(1)).close();
	}

	@Test
	public void test_StopFeeder_whenSubscriberFoundInContext() throws BitsoSubscribeException {
		when(context.getBitsoSubscriber()).thenReturn(null);
		SubscribeFeeder<UpdatedOrderBook> diffOrderOnBook = new DiffOrderOnBookSubscribeFeederImpl("whatever", context,
				subscribeDiffOrderCommandExecutor, orderBookRESTProvideToContextCommandExecutor,
				mergeIntoOrderBookCommandExecutor);
		diffOrderOnBook.stopFeeding();

		verify(bitsoSubscriber, VerificationModeFactory.noMoreInteractions()).close();
	}

	private void test_successFeedingStartSerie(int updatesOnSubscriptionUpdater) throws SonarChallengeException {
		SubscriptionUpdater<UpdatedOrderBook> subscriptionUpdater = (SubscriptionUpdater<UpdatedOrderBook>) mock(
				SubscriptionUpdater.class);
		SubscribeFeeder<UpdatedOrderBook> diffOrderOnBook = new DiffOrderOnBookSubscribeFeederImpl("whatever", context,
				subscribeDiffOrderCommandExecutor, orderBookRESTProvideToContextCommandExecutor,
				mergeIntoOrderBookCommandExecutor);
		diffOrderOnBook.subscribe(subscriptionUpdater);

		diffOrderOnBook.startFeeding();
		diffOrderOnBook.stopFeeding();

		verify(subscribeDiffOrderCommandExecutor, VerificationModeFactory.times(1)).execute();
		verify(mergeIntoOrderBookCommandExecutor, VerificationModeFactory.times(1)).execute();
		verify(orderBookRESTProvideToContextCommandExecutor, VerificationModeFactory.times(1)).execute();
		verify(subscriptionUpdater, VerificationModeFactory.times(updatesOnSubscriptionUpdater))
				.onUpdate(any(UpdatedOrderBook.class));
	}

}
