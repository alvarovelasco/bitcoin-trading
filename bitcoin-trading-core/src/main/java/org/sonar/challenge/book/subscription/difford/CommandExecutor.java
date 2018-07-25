package org.sonar.challenge.book.subscription.difford;

import org.sonar.challenge.exception.SonarChallengeException;

public interface CommandExecutor {

	void execute() throws SonarChallengeException;
	
}
