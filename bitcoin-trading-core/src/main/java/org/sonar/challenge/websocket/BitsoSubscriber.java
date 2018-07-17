package org.sonar.challenge.websocket;

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.PathParam;

/**
 * 
 * @author Alvaro
 *
 */
@ClientEndpoint
public class BitsoSubscriber {

	private Optional<Handler> handler = Optional.empty();
	
	private final String book;
	
	private final String type;
	
	private Session session;
	
	private final static String ENDPOINT = "wss://ws.bitso.com";
	
	private final static String SUBSCRIBE_ACTION = "{ action: 'subscribe', book: '{0}', type: '{1}' }";

	public BitsoSubscriber(String book, String type) {
		this.book = requireNonNull(book);
		this.type = requireNonNull(type);
	}

	public void subscribe() {
		WebSocketContainer container = null;
		try {		
			container = ContainerProvider.getWebSocketContainer();
			session = container.
					connectToServer(this, URI.create(ENDPOINT));

		} catch (DeploymentException | IOException e) {
			// FIXME throw restart exception
			e.printStackTrace();
		} finally {
		}
	}

	public void setHandler(Handler handler) {
		this.handler = Optional.ofNullable(handler);
	}
	
	@OnOpen
	public void onOpen(Session session, @PathParam("response") String response) throws IOException  {
		System.err.println(response + " " + session.getId());
		this.session = session;
		session.getBasicRemote().sendText(
				String.format(SUBSCRIBE_ACTION, book, type));
		
	}
	 
 
	@OnClose
	public void close() throws IOException {
		System.err.println(" CLOSE ");
		session.close();
	}
	
	@OnMessage
	public void onMessage(String message) {
		System.err.println(" MESSAGE " + message);
		handler.orElse(m -> {
		}).handle(message);
	}

	public static interface Handler {
		void handle(String message);
	}
}
