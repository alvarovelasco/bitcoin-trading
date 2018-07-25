package org.sonar.challenge.exception;

public class OrderBookNotFoundException extends SonarChallengeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 353L;
	
	public OrderBookNotFoundException(String msg) {
		super(msg);
	}
}
