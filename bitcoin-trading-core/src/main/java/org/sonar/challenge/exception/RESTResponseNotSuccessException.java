package org.sonar.challenge.exception;

import org.apache.http.StatusLine;

public final class RESTResponseNotSuccessException extends SonarChallengeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RESTResponseNotSuccessException(StatusLine statusLine) {
		super("Failed : HTTP error code : "
				   + statusLine.getStatusCode());
	}
	
}
