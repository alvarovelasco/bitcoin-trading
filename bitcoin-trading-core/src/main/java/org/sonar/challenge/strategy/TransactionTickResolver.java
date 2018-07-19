package org.sonar.challenge.strategy;

import java.util.Optional;

import org.sonar.challenge.book.Transaction;

public interface TransactionTickResolver {
	Optional<Ticks> resolve(Transaction c1, Transaction c2);
}
