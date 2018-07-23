package org.sonar.challenge.exception;

public abstract class SonarChallengeException extends Exception {

	private static final long serialVersionUID = -5088254866087889126L;

	public SonarChallengeException(String msg) {
		super(msg);
	}
	
	public SonarChallengeException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public SonarChallengeException(Throwable t) {
		super(t);
	}
}
