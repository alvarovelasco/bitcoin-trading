package org.sonar.challenge.strategy;

import java.util.Objects;
import java.util.Optional;

import org.sonar.challenge.book.Transaction;

public class TransactionTickResolverImpl implements TransactionTickResolver {

	@Override
	public Optional<Ticks> resolve(Transaction c1, Transaction c2) {
		Objects.requireNonNull(c1);
		Objects.requireNonNull(c2);
		
		int signum = c1.getPrice().compareTo(c2.getPrice());
		
		if (signum > 0) return Optional.of(Ticks.UPTICK);
		else if (signum < 0) return Optional.of(Ticks.DOWNTICK);
		
		return Optional.empty();
	}

}
