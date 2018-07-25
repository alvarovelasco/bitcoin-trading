package org.sonar.challenge.book.subscription.difford;

import static java.util.Objects.requireNonNull;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.json.OrderBookDecoder;
import org.sonar.challenge.book.json.TransformerFactory;
import org.sonar.challenge.book.json.TransformerFactory.Transformer;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.rest.BitsoOrderBookRESTRequest;
import org.sonar.challenge.rest.SimpleRESTRequest;
import org.sonar.challenge.util.GSonBuilder;

/**
 * Executes the simple rest request and transform the result into an {@link OrderBook} 
 * that is set into the {@link CreateDiffOrderForBookUpdateCommandContext}
 * 
 * @author Alvaro
 *
 */
public class OrderBookRESTProvideToContextCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;
	
	private final SimpleRESTRequest simpleRESTRequest;
	
	private final Transformer<OrderBookDecoder, OrderBook> transformer;
	
	/**
	 * 
	 * @param context
	 * @param bookName
	 */
	public OrderBookRESTProvideToContextCommandExecutor(CreateDiffOrderForBookUpdateCommandContext context,
			String bookName) {
		this(context, TransformerFactory.getInstance().getOrderBookDecoderTransformer(bookName),
				new BitsoOrderBookRESTRequest(bookName));
	}
	
	/**
	 * Constructor used for test purposes
	 * @param context
	 * @param transformer
	 * @param simpleRESTRequest
	 */
	OrderBookRESTProvideToContextCommandExecutor(CreateDiffOrderForBookUpdateCommandContext context,
			Transformer<OrderBookDecoder, OrderBook> transformer,
			SimpleRESTRequest simpleRESTRequest) {
		this.context = requireNonNull(context);
		this.simpleRESTRequest = requireNonNull(simpleRESTRequest);
		this.transformer = requireNonNull(transformer);
	}
	
	public void execute() throws SonarChallengeException {
		String content = simpleRESTRequest.request();
		
		OrderBookDecoder decoder = GSonBuilder.buildStandardGson().fromJson(content, OrderBookDecoder.class);
		OrderBook orderBook = transformer.transform(decoder);
		context.setOrderBook(orderBook);
	}

}
