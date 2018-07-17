package org.sonar.challenge.websocket;

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.PathParam;

import org.glassfish.tyrus.client.ClientManager;
import org.sonar.challenge.book.net.json.DiffOrderDecoder;

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


	public BitsoSubscriber(String book, SubscriptionTypes type,MessageCoder<D> coder) {
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
		System.out.println(" " + session.getId());
		this.session = session;
		System.out.println(new Gson().toJson(new Subscribe(book, type)));
		session.getAsyncRemote().sendText(new Gson().toJson(new Subscribe(book, type)));

	}

	@OnClose
	public void close() throws IOException {
		System.out.println(" CLOSE ");
		session.close();
	}

	@OnMessage
	public void onMessage(String message) {
		System.out.println(" MESSAGE " + message);
		handler.orElse(m -> {}).handle(coder.code(message));
	}

	public static interface Handler<D> {
		void handle(D d);
	}

	public static void main(String[] args) {
		BitsoSubscriber<DiffOrderDecoder> subscriber = new BitsoSubscriber<>("btc_mxn", 
				SubscriptionTypes.DIFF_ORDERS, new DiffOrderMessageCoder());
		subscriber.setHandler(m -> System.out.println(m));
		subscriber.subscribe();

	}
	
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
