package org.sonar.challenge.book.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.DiffOrderDecoder.PayloadOrder;
import org.sonar.challenge.book.json.OrderBookDecoder.Order;
import org.sonar.challenge.book.json.OrderBookDecoder.Payload;
import org.sonar.challenge.websocket.SubscriptionTypes;

public class TransformerFactoryTest {

	private final static String BOOK_NAME = "whatever";
	
	private final static String UPDATED_AT = "2018-07-21T23:30:31.000+00:00";
	
	@Test
	public void test_getOrderBookDecoderTransformer_returningObjectTransformerExisting() {
		assertNotNull(TransformerFactory.getInstance().getOrderBookDecoderTransformer(BOOK_NAME));
	}

	@Test(expected=NullPointerException.class)
	public void test_getOrderBookDecoderTransformer_whenErrorWithNullSequence() {
		OrderBookDecoder orderBookDecoder = 
				new OrderBookDecoder(new Payload(null, null, null, UPDATED_AT));
		TransformerFactory.getInstance().getOrderBookDecoderTransformer(BOOK_NAME).transform(orderBookDecoder);
	}

	@Test
	public void test_getOrderBookDecoderTransformer_processedWithOneBidOrder() {
		OrderBookDecoder orderBookDecoder = 
				new OrderBookDecoder(new Payload("1", Arrays.asList(new Order(BOOK_NAME, BigDecimal.ONE, BigDecimal.ONE)),
						new ArrayList<>(), UPDATED_AT));
		OrderBook transformed =
				TransformerFactory.getInstance().getOrderBookDecoderTransformer(BOOK_NAME).transform(orderBookDecoder);
		assertEquals(1, transformed.getBids().size());
		assertEquals(0, transformed.getAsks().size());
	}

	@Test
	public void test_getOrderBookDecoderTransformer_processedWithOneAskOrder() {
		OrderBookDecoder orderBookDecoder = 
				new OrderBookDecoder(new Payload("1",
						new ArrayList<>(),  
						Arrays.asList(new Order(BOOK_NAME, BigDecimal.ONE, BigDecimal.ONE)),UPDATED_AT));
		OrderBook transformed =
				TransformerFactory.getInstance().getOrderBookDecoderTransformer(BOOK_NAME).transform(orderBookDecoder);
		assertEquals(0, transformed.getBids().size());
		assertEquals(1, transformed.getAsks().size());
	}

	@Test
	public void test_getDiffOrderDecoderTransformer_returningObjectTransformerExisting() {
		assertNotNull(TransformerFactory.getInstance().getDiffOrderDecoderTransformer());
	}

	@Test
	public void test_getDiffOrderDecoderTransformer_whenBuyOrder_withNoOpenState() {
		DiffOrderDecoder diffOrderDecoder = get(1);
		diffOrderDecoder.setPayload(Arrays.asList(new PayloadOrder(BigDecimal.ONE, BigDecimal.ONE, DiffOrderMessageType.BUY, DiffOrderState.CANCELLED, 2l)));
		OrderBook transformed = TransformerFactory.getInstance().getDiffOrderDecoderTransformer().transform(diffOrderDecoder);
		assertNotNull(transformed);
		assertEquals(transformed.getName(), BOOK_NAME);
		assertEquals(0, transformed.getBids().size());
		assertEquals(0, transformed.getAsks().size());
	}

	@Test
	public void test_getDiffOrderDecoderTransformer_whenSellOrder_withNoOpenState() {
		DiffOrderDecoder diffOrderDecoder = get(1);
		diffOrderDecoder.setPayload(Arrays.asList(new PayloadOrder(BigDecimal.ONE, BigDecimal.ONE, DiffOrderMessageType.SELL, DiffOrderState.CANCELLED, 2l)));
		OrderBook transformed = TransformerFactory.getInstance().getDiffOrderDecoderTransformer().transform(diffOrderDecoder);
		assertNotNull(transformed);
		assertEquals(BOOK_NAME, transformed.getName());
		assertEquals(0, transformed.getBids().size());
		assertEquals(0, transformed.getAsks().size());
	}

	@Test(expected=NullPointerException.class)
	public void test_getDiffOrderDecoderTransformer_whenBookNameNotPresent_thenErrorWillAppear() {
		DiffOrderDecoder diffOrderDecoder = get(1);
		diffOrderDecoder.setBook(null);
		TransformerFactory.getInstance().getDiffOrderDecoderTransformer().transform(diffOrderDecoder);
	}
	
	@Test
	public void test_getDiffOrderDecoderTransformer_whenOpenSellOrderFound() {
		DiffOrderDecoder diffOrderDecoder = get(1);
		diffOrderDecoder.setPayload(Arrays.asList(new PayloadOrder(BigDecimal.ONE, BigDecimal.ONE, DiffOrderMessageType.SELL, DiffOrderState.OPEN, 2l)));
		OrderBook transformed = TransformerFactory.getInstance().getDiffOrderDecoderTransformer().transform(diffOrderDecoder);
		assertNotNull(transformed);
		assertEquals(BOOK_NAME, transformed.getName());
		assertEquals(0, transformed.getAsks().size());
		assertEquals(1, transformed.getBids().size());
	}
	
	private DiffOrderDecoder get(long sequence) {
		DiffOrderDecoder diffOrderDecoder = new DiffOrderDecoder();
		diffOrderDecoder.setBook(BOOK_NAME);
		diffOrderDecoder.setSequence(String.valueOf(sequence));
		diffOrderDecoder.setType(SubscriptionTypes.DIFF_ORDERS.getKeyword());
		diffOrderDecoder.setPayload(new ArrayList<>());
		return diffOrderDecoder;
	}
}
