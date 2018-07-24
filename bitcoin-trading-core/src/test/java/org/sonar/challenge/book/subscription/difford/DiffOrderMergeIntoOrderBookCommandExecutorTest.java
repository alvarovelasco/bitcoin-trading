package org.sonar.challenge.book.subscription.difford;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.book.json.DiffOrderDecoder.PayloadOrder;
import org.sonar.challenge.book.json.DiffOrderMessageType;
import org.sonar.challenge.book.json.DiffOrderState;
import org.sonar.challenge.book.subscription.difford.DiffOrderMergeIntoOrderBookCommandExecutor.DiffOrderDecoderListProvider;
import org.sonar.challenge.exception.OrderBookNotFoundException;
import org.sonar.challenge.exception.SonarChallengeException;

public class DiffOrderMergeIntoOrderBookCommandExecutorTest {

	@Mock
	private CreateDiffOrderForBookUpdateCommandContext context;

	private OrderBook initialOrderBook = new OrderBook("whatever", 100);

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(context.getOrderBook()).thenReturn(initialOrderBook);
	}

	@Test(expected = OrderBookNotFoundException.class)
	public void testMerge_whenNoOrderBookDefinedOnContext() throws SonarChallengeException {
		when(context.getOrderBook()).thenReturn(null);

		DiffOrderDecoderListProvider listProvider = mock(DiffOrderDecoderListProvider.class);
		new DiffOrderMergeIntoOrderBookCommandExecutor(context, listProvider).execute();

		verify(context, VerificationModeFactory.noMoreInteractions()).setOrderBook(any(OrderBook.class));
		verify(context, VerificationModeFactory.times(1)).getOrderBook();
		verify(listProvider, VerificationModeFactory.noMoreInteractions()).provide();
	}

	@Test(expected = NullPointerException.class)
	public void testMerge_whenNoDiffOrderDecoderListProviderDefined() throws SonarChallengeException {
		new DiffOrderMergeIntoOrderBookCommandExecutor(context, null).execute();
	}

	@Test(expected = NullPointerException.class)
	public void testMerge_whenNoContextDefined() throws SonarChallengeException {
		new DiffOrderMergeIntoOrderBookCommandExecutor(null, () -> null).execute();
	}

	@Test
	public void testMerge_whenSequenceInPast() throws SonarChallengeException {
		context = new CreateDiffOrderForBookUpdateCommandContext();
		context.setOrderBook(initialOrderBook);

		DiffOrderDecoderListProvider listProvider = () -> {
			return Arrays.asList(getDiffOrderDecoder(initialOrderBook.getLastSequenceEntered() - 1, 
					Optional.of(DiffOrderMessageType.BUY), DiffOrderState.OPEN));
		};

		new DiffOrderMergeIntoOrderBookCommandExecutor(context, listProvider).execute();
		OrderBook resultingOrderBook = context.getOrderBook();
		Assert.assertEquals(initialOrderBook, resultingOrderBook);
	}

	@Test
	public void testMerge_whenDiffOrderDecoderListHasOneElement() throws SonarChallengeException {
		context = new CreateDiffOrderForBookUpdateCommandContext();
		context.setOrderBook(initialOrderBook);

		DiffOrderDecoderListProvider listProvider = () -> {

			return Arrays.asList(getDiffOrderDecoder(initialOrderBook.getLastSequenceEntered() + 1, 
					Optional.of(DiffOrderMessageType.BUY), DiffOrderState.COMPLETED));
		};

		new DiffOrderMergeIntoOrderBookCommandExecutor(context, listProvider).execute();

		OrderBook resultingOrderBook = context.getOrderBook();
		Assert.assertNotEquals(initialOrderBook, resultingOrderBook);
		Assert.assertNotEquals(initialOrderBook.getLastSequenceEntered(), resultingOrderBook.getLastSequenceEntered());
		Assert.assertEquals(0, resultingOrderBook.getBids().size());
		Assert.assertEquals(1, resultingOrderBook.getAsks().size());
	}

	@Test
	public void testMerge_whenDiffOrderDecoderListHasOneElementCancelled() throws SonarChallengeException {
		context = new CreateDiffOrderForBookUpdateCommandContext();
		context.setOrderBook(initialOrderBook);

		DiffOrderDecoderListProvider listProvider = () -> {
			return Arrays.asList(getDiffOrderDecoder(initialOrderBook.getLastSequenceEntered() + 1, 
					Optional.of(DiffOrderMessageType.BUY), DiffOrderState.CANCELLED));
		};

		new DiffOrderMergeIntoOrderBookCommandExecutor(context, listProvider).execute();

		OrderBook resultingOrderBook = context.getOrderBook();
		Assert.assertNotEquals(initialOrderBook, resultingOrderBook);
		Assert.assertEquals(initialOrderBook.getName(), resultingOrderBook.getName());
		Assert.assertNotEquals(initialOrderBook.getLastSequenceEntered(), resultingOrderBook.getLastSequenceEntered());
		Assert.assertEquals(0, resultingOrderBook.getBids().size());
		Assert.assertEquals(0, resultingOrderBook.getAsks().size());
	}

	private DiffOrderDecoder getDiffOrderDecoder(long sequence, Optional<DiffOrderMessageType> messageType,
			DiffOrderState orderState) {
		DiffOrderDecoder diffOrderDecoder = new DiffOrderDecoder();
		diffOrderDecoder.setBook("whatever");
		diffOrderDecoder.setSequence(String.valueOf(sequence));

		if (messageType.isPresent()) {
			PayloadOrder order = new PayloadOrder(BigDecimal.ONE, BigDecimal.ONE, messageType.get(), orderState,
					LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
			diffOrderDecoder.setPayload(Arrays.asList(order));
		}

		return diffOrderDecoder;
	}
}
