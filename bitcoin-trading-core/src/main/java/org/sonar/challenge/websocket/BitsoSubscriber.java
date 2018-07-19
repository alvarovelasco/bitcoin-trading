package org.sonar.challenge.websocket;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.glassfish.tyrus.client.ClientManager;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;
import org.sonar.challenge.util.GSonBuilder;

import com.google.gson.Gson;

/**
 * 
 * @author Alvaro
 *
 */
@ClientEndpoint
public class BitsoSubscriber<D> {

	private Optional<Handler<D>> handler = Optional.empty();

	private final String book;

	private final SubscriptionTypes type;

	private final MessageCoder<D> coder;
	
	private Session session;

	private final static String ENDPOINT = "wss://ws.bitso.com";


	public BitsoSubscriber(String book, SubscriptionTypes type, MessageCoder<D> coder) {
		this.book = requireNonNull(book);
		this.type = requireNonNull(type);
		this.coder = requireNonNull(coder);
	}

	public void subscribe() {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			ClientManager clientManager = (ClientManager) container;
			session = clientManager.asyncConnectToServer(this, URI.create(ENDPOINT)).get();
		} catch (DeploymentException | InterruptedException | ExecutionException e) {
			// FIXME throw restart exception
			e.printStackTrace();
		} finally {
		}
	}

	public synchronized void setHandler(Handler<D> handler) {
		this.handler = Optional.ofNullable(handler);
	}

	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		String subscribeRequestMsg = GSonBuilder.buildStandardGson().toJson(new Subscribe(book, type));
		session.getAsyncRemote().sendText(subscribeRequestMsg);

	}

	@OnClose
	public void close() throws IOException {
		session.close();
	}

	@OnMessage
	public void onMessage(String message) {
		handler.orElse(m -> {}).handle(coder.code(message));
	}

	public static interface Handler<D> {
		void handle(D d);
	}

	public static void main(String[] args) {
		BitsoSubscriber<DiffOrderDecoder> subscriber = new BitsoSubscriber<>("btc_mxn", 
				SubscriptionTypes.DIFF_ORDERS, 
				m -> GSonBuilder.buildStandardGson().fromJson(m, DiffOrderDecoder.class));
		subscriber.setHandler(m -> System.out.println(m));
		subscriber.subscribe();

	}

	@SuppressWarnings("unused")
	private class Subscribe { 
		private final String action = "subscribe";
		
		private final String type ;
		
		private final String book;
		
		public Subscribe(String book, SubscriptionTypes type) {
			this.book = requireNonNull(book);
			this.type = requireNonNull(type).getKeyword();
		}
		
	}
}
