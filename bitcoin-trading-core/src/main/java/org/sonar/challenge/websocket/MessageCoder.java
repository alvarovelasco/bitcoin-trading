package org.sonar.challenge.websocket;

public interface MessageCoder<C> {

	C code(String message);
	
	Class<C> getObjClass();
}
