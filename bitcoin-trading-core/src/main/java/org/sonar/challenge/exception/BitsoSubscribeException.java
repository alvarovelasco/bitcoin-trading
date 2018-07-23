package org.sonar.challenge.exception;

public final class BitsoSubscribeException extends SonarChallengeException {

	private static final long serialVersionUID = 12424L;
			
	public BitsoSubscribeException(Throwable t) {
		super("", t);
	}
		
}
