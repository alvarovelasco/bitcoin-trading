package org.sonar.challenge.book.subscription.difford;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.TransformerFactory;
import org.sonar.challenge.exception.RESTResponseNotSuccessException;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.rest.SimpleRESTRequest;

public class OrderBookRESTProvideToContextCommandExecutorTest {

	private final String response = "{" + 
			"    \"success\": true,\n" + 
			"    \"payload\": {\n" + 
			"        \"asks\": [{\n" + 
			"            \"book\": \"btc_mxn\",\n" + 
			"            \"price\": \"5632.24\",\n" + 
			"            \"amount\": \"1.34491802\"\n" + 
			"        },{\n" + 
			"            \"book\": \"btc_mxn\",\n" + 
			"            \"price\": \"5633.44\",\n" + 
			"            \"amount\": \"0.4259\"\n" + 
			"        },{\n" + 
			"            \"book\": \"btc_mxn\",\n" + 
			"            \"price\": \"5642.14\",\n" + 
			"            \"amount\": \"1.21642\"\n" + 
			"        }],\n" + 
			"        \"bids\": [{\n" + 
			"            \"book\": \"btc_mxn\",\n" + 
			"            \"price\": \"6123.55\",\n" + 
			"            \"amount\": \"1.12560000\"\n" + 
			"        },{\n" + 
			"            \"book\": \"btc_mxn\",\n" + 
			"            \"price\": \"6121.55\",\n" + 
			"            \"amount\": \"2.23976\"\n" + 
			"        }],\n" + 
			"        \"updated_at\": \"2016-04-08T17:52:31.000+00:00\",\n" + 
			"        \"sequence\": \"27214\"\n" + 
			"    }\n" + 
			"}";
	
	@Mock
	private SimpleRESTRequest mockedRequest;
	
	@Mock
	private CreateDiffOrderForBookUpdateCommandContext context;
	
	@Before
	public void init() throws SonarChallengeException {
		MockitoAnnotations.initMocks(this);
		
		when(mockedRequest.request()).thenReturn(response);
	}
	
	@Test
	public void test_successfulOrderBookResponse() throws SonarChallengeException {
		new OrderBookRESTProvideToContextCommandExecutor(context,
					TransformerFactory.getInstance().getOrderBookDecoderTransformer("whatever"),
					mockedRequest					
					).execute();
		
		verify(mockedRequest, VerificationModeFactory.times(1)).request();
		verify(context, VerificationModeFactory.times(1)).setOrderBook(any(OrderBook.class));
	}
	
	
	@SuppressWarnings("unchecked")
	@Test(expected=RESTResponseNotSuccessException.class)
	public void test_exceptionCaughtOnRESTRequest() throws SonarChallengeException {
		when(mockedRequest.request()).thenThrow(RESTResponseNotSuccessException.class);
		
		new OrderBookRESTProvideToContextCommandExecutor(context,
					TransformerFactory.getInstance().getOrderBookDecoderTransformer("whatever"),
					mockedRequest					
					).execute();
		
		verify(mockedRequest, VerificationModeFactory.times(1)).request();
		verify(context, VerificationModeFactory.noMoreInteractions()).setOrderBook(any(OrderBook.class));
	}
	
}
