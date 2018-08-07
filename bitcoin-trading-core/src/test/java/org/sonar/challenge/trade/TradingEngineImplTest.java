package org.sonar.challenge.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.challenge.book.Trade;
import org.sonar.challenge.exception.RESTResponseNotSuccessException;
import org.sonar.challenge.order.OrderBatch;
import org.sonar.challenge.order.OrderIssuer;
import org.sonar.challenge.rest.SimpleRESTRequest;
import org.sonar.challenge.strategy.DefaultTradingStrategy;
import org.sonar.challenge.strategy.DefaultTradingStrategyFactory;

public class TradingEngineImplTest {

	private static final int TRADE_LIMIT = 10;

	private static final String STANDARD_SUCCESSFUL_TRADE_RESPONSE = "{\n" + "    \"success\": true,\n"
			+ "    \"payload\": [{\n" + "        \"book\": \"btc_mxn\",\n"
			+ "        \"created_at\": \"2016-04-08T17:52:31+0000\",\n" + "        \"amount\": \"0.02000000\",\n"
			+ "        \"maker_side\": \"buy\",\n" + "        \"price\": \"5545.01\",\n" + "        \"tid\": 55845\n"
			+ "    }, {\n" + "        \"book\": \"btc_mxn\",\n"
			+ "        \"created_at\": \"2016-04-08T17:52:31+0000\",\n" + "        \"amount\": \"0.33723939\",\n"
			+ "        \"maker_side\": \"sell\",\n" + "        \"price\": \"5633.98\",\n" + "        \"tid\": 55844\n"
			+ "    }]\n" + "}";

	private static final String SECONDARY_STANDARD_SUCCESSFUL_TRADE_RESPONSE = "{\n" + "    \"success\": true,\n"
			+ "    \"payload\": [{\n" + "        \"book\": \"btc_mxn\",\n"
			+ "        \"created_at\": \"2016-04-08T17:52:31+0000\",\n" + "        \"amount\": \"0.02000000\",\n"
			+ "        \"maker_side\": \"buy\",\n" + "        \"price\": \"5545.01\",\n" + "        \"tid\": 55845\n"
			+ "    }, {\n" + "        \"book\": \"btc_mxn\",\n"
			+ "        \"created_at\": \"2016-04-08T17:52:31+0000\",\n" + "        \"amount\": \"0.33723939\",\n"
			+ "        \"maker_side\": \"sell\",\n" + "        \"price\": \"5633.98\",\n" + "        \"tid\": 55844\n"
			+ "    },  {\\n\" + \n" + "			\"book\": \"btc_mxn\",\n"
			+ "			\"created_at\": \"2016-04-08T17:52:51+0000\",\n"
			+ "			\"amount\": \"0.33723939\",\n" + "			\"maker_side\": \"sell\",\n"
			+ "			\"price\": \"5800.98\",\n" + "			\"tid\": 55846\n" + "			\"    }]\n" + "}";

	@Mock
	private SimpleRESTRequest mockedRestRequest;

	@Mock
	private DefaultTradingStrategyFactory defaultTradingStrategyFactoryMock;

	// FIXME AVF: Check whether it should be final or not (now it cannot be final if
	// we want to verify)
	@Mock
	private DefaultTradingStrategy defaultTradingStrategyMock;
	
	@Mock
	private OrderIssuer orderIssuer;

	private class AssertTestListener implements TradingEngineListener {

		private TradingEngineListener wrapperListener;

		public AssertTestListener(TradingEngineListener wrapperListener) {
			this.wrapperListener = wrapperListener;
		}

		public void setWrapperListener(TradingEngineListener wrapperListener) {
			this.wrapperListener = wrapperListener;
		}

		@Override
		public void onTradeListChange(List<Trade> oldList, List<Trade> newTradeList) {
			wrapperListener.onTradeListChange(oldList, newTradeList);
		}
	}
	
	private TradingEngineImpl getTradingEngine(String response) throws RESTResponseNotSuccessException {
		when(mockedRestRequest.request()).thenReturn(response);
		TradingEngineImpl engineImpl = new TradingEngineImpl(mockedRestRequest, TRADE_LIMIT);

		return engineImpl;
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		doReturn(defaultTradingStrategyMock).when(defaultTradingStrategyFactoryMock).getStrategy(anyVararg());
		doReturn(new OrderBatch(Arrays.asList())).when(defaultTradingStrategyMock).resolveOrders();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_whenExecutedForTheFirstTime()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		engineImpl.addListener((oldL, newL) -> {
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});
		engineImpl.run();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_whenExecutedFor2Times_withNoNewTrades()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);

		AssertTestListener assertListener = new AssertTestListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addListener(assertListener);
		engineImpl.run();

		assertListener.setWrapperListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.run();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_whenExecutedFor2Times_withOneNewTrade()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);

		AssertTestListener assertListener = new AssertTestListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addListener(assertListener);
		engineImpl.run();

		when(mockedRestRequest.request()).thenReturn(SECONDARY_STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		assertListener.setWrapperListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertNotNull(newL);
			assertEquals(2, oldL.size());
			assertEquals(3, newL.size());
		});

		engineImpl.run();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedTradingStrategy_whenExecutedForTheFirstTime()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		engineImpl.addListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addTradingStrategyFactory(defaultTradingStrategyFactoryMock);
		engineImpl.run();

		verify(defaultTradingStrategyFactoryMock).getStrategy(anyVararg());
		verify(defaultTradingStrategyMock).resolveOrders();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withDefaultTradingStrategy_whenExecutedForTheFirstTime()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		engineImpl.addListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addTradingStrategyFactory(new DefaultTradingStrategyFactory());

		engineImpl.run();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedTradingStrategy_whenExecuted2Times()
			throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		AssertTestListener assertListener = new AssertTestListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});
		engineImpl.addListener(assertListener);
		engineImpl.addTradingStrategyFactory(new DefaultTradingStrategyFactory());

		engineImpl.run();

		when(mockedRestRequest.request()).thenReturn(SECONDARY_STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		assertListener.setWrapperListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertNotNull(newL);
			assertEquals(2, oldL.size());
			assertEquals(3, newL.size());
		});

		engineImpl.run();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedOrderIssuer_whenExecutedForTheFirstTime() throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		engineImpl.addListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addOrderIssuer(orderIssuer);
		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedOrderIssuer_whenExecuted2Times() throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		AssertTestListener assertListener = new AssertTestListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});
		engineImpl.addListener(assertListener);

		engineImpl.addOrderIssuer(orderIssuer);
		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());

		when(mockedRestRequest.request()).thenReturn(SECONDARY_STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		assertListener.setWrapperListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertNotNull(newL);
			assertEquals(2, oldL.size());
			assertEquals(3, newL.size());
		});

		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedTradingStrategyAndOrderIssuer_whenExecutedForTheFirstTime() throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		engineImpl.addListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});

		engineImpl.addTradingStrategyFactory(defaultTradingStrategyFactoryMock);
		engineImpl.addOrderIssuer(orderIssuer);
		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());
		verify(defaultTradingStrategyFactoryMock).getStrategy(anyVararg());
		verify(defaultTradingStrategyMock).resolveOrders();
	}

	@Test
	public void test_receiveStandardSuccessfulTradeResponse_withMockedTradingStrategyAndOrderIssuer_whenExecutedFor2Times_withOneNewTrade() throws RESTResponseNotSuccessException {
		TradingEngineImpl engineImpl = getTradingEngine(STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		AssertTestListener assertListener = new AssertTestListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertTrue(oldL.isEmpty());
			assertNotNull(newL);
			assertEquals(2, newL.size());
		});
		engineImpl.addListener(assertListener);
		engineImpl.addTradingStrategyFactory(defaultTradingStrategyFactoryMock);
		engineImpl.addOrderIssuer(orderIssuer);
		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());
		verify(defaultTradingStrategyFactoryMock).getStrategy(anyVararg());
		verify(defaultTradingStrategyMock).resolveOrders();

		when(mockedRestRequest.request()).thenReturn(SECONDARY_STANDARD_SUCCESSFUL_TRADE_RESPONSE);
		assertListener.setWrapperListener((oldL, newL) -> {
			assertNotNull(oldL);
			assertNotNull(newL);
			assertEquals(2, oldL.size());
			assertEquals(3, newL.size());
		});

		engineImpl.run();

		verify(orderIssuer, VerificationModeFactory.times(1)).issue(any());
		verify(defaultTradingStrategyFactoryMock).getStrategy(anyVararg());
		verify(defaultTradingStrategyMock).resolveOrders();
	}
}
