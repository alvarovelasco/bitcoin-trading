package org.sonar.challenge.book.net.json;

public enum DiffOrderMessageType {
	BUY(0), SELL(1);

	private final int number;
	
	private DiffOrderMessageType(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
}
