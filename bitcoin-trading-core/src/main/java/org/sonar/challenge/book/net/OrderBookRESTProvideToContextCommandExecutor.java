package org.sonar.challenge.book.net;

import static java.util.Objects.requireNonNull;

import org.sonar.challenge.book.OrderBook;
import org.sonar.challenge.book.net.json.OrderBookDecoder;
import org.sonar.challenge.book.net.json.TransformerFactory.Transformer;
import org.sonar.challenge.rest.SimpleRESTRequest;
import org.sonar.challenge.util.GSonBuilder;

import com.google.gson.Gson;

/**
 * Executes the simple rest request and transform the result into an {@link OrderBook} 
 * that is set into the {@link CreateDiffOrderForBookUpdateCommandContext}
 * 
 * @author Alvaro
 *
 */
public final class OrderBookRESTProvideToContextCommandExecutor implements CommandExecutor {

	private final CreateDiffOrderForBookUpdateCommandContext context;
	
	private final SimpleRESTRequest simpleRESTRequest;
	
	private final Transformer<OrderBookDecoder, OrderBook> transformer;
	
	public OrderBookRESTProvideToContextCommandExecutor(CreateDiffOrderForBookUpdateCommandContext context,
			Transformer<OrderBookDecoder, OrderBook> transformer,
			SimpleRESTRequest simpleRESTRequest) {
		this.context = requireNonNull(context);
		this.simpleRESTRequest = requireNonNull(simpleRESTRequest);
		this.transformer = requireNonNull(transformer);
	}
	
	public void execute() {
		String content = simpleRESTRequest.request();
		
		OrderBookDecoder decoder = GSonBuilder.buildStandardGson().fromJson(content, OrderBookDecoder.class);
		OrderBook orderBook = transformer.transform(decoder);
		context.setOrderBook(orderBook);
	}

}
