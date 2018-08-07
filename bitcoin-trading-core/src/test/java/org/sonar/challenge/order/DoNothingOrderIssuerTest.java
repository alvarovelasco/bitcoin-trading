package org.sonar.challenge.order;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;

public class DoNothingOrderIssuerTest {

	@Mock
	private OrderIssuerListener orderIssuerListener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIssue_whenNullOrderBatchProvided() {
		OrderIssuer impl = new DoNothingOrderIssuerImpl();

		OrderBatch orderBatch = null;

		impl.addListener(orderIssuerListener);
		impl.issue(orderBatch);

		assertIssue(orderIssuerListener, null);
	}

	@Test
	public void testIssue_whenEmptyOrderBatchProvided() {
		OrderIssuer impl = new DoNothingOrderIssuerImpl();

		OrderBatch orderBatch = new OrderBatch(new ArrayList<>());

		impl.addListener(orderIssuerListener);
		impl.issue(orderBatch);

		assertIssue(orderIssuerListener, null);
	}

	@Test
	public void testIssue_whenOneOrderInBatchProvided() {
		OrderIssuer impl = new DoNothingOrderIssuerImpl();

		List<Order> orders = Arrays
				.asList(new Order(BigDecimal.ONE, BigDecimal.ONE, OrderType.BUY, LocalDateTime.now()));
		OrderBatch orderBatch = new OrderBatch(orders);

		impl.addListener(orderIssuerListener);
		impl.issue(orderBatch);

		assertIssue(orderIssuerListener, orders);
	}

	@Test
	public void testIssue_whenOneOrderAndNullItemInBatchProvided() {
		OrderIssuer impl = new DoNothingOrderIssuerImpl();

		// order batch.getNewOrderstoPlace invoked
		// mock listener invoked once
		List<Order> orders = Arrays
				.asList(new Order(BigDecimal.ONE, BigDecimal.ONE, OrderType.BUY, LocalDateTime.now()), null);
		OrderBatch orderBatch = new OrderBatch(orders);

		impl.addListener(orderIssuerListener);
		impl.issue(orderBatch);

		assertIssue(orderIssuerListener, orders);
	}

	@Test
	public void testIssue_whenMultipleOrderInBatchProvided() {
		OrderIssuer impl = new DoNothingOrderIssuerImpl();

		// order batch.getNewOrderstoPlace invoked
		// mock listener invoked multiple (2)

		List<Order> orders = Arrays.asList(
				new Order(BigDecimal.ONE, BigDecimal.ONE, OrderType.BUY, LocalDateTime.now()),
				new Order(BigDecimal.ONE, BigDecimal.ONE, OrderType.SELL, LocalDateTime.now()));
		OrderBatch orderBatch = new OrderBatch(orders);

		impl.addListener(orderIssuerListener);
		impl.issue(orderBatch);

		assertIssue(orderIssuerListener, orders);
	}

	private void assertIssue(OrderIssuerListener listener, List<Order> orders) {
		// clean up any null item in the list, or set an empty list if not defined.
		if (orders != null) {
			orders = orders.stream().filter(o -> o != null).collect(Collectors.toList());
		} else {
			orders = new ArrayList<>();
		}

		int expectedNTimesInvoked = orders.size();

		verify(listener, VerificationModeFactory.times(expectedNTimesInvoked)).beforeIssuingOrder(any(Order.class));
		verify(listener, VerificationModeFactory.times(expectedNTimesInvoked)).afterIssuingOrder(any(Order.class));

		orders.stream().forEach(o -> {
			verify(listener, VerificationModeFactory.times(1)).beforeIssuingOrder(eq(o));
			verify(listener, VerificationModeFactory.times(1)).afterIssuingOrder(eq(o));
		});
	}
}
