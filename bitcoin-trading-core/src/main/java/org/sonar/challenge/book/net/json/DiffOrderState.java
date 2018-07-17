package org.sonar.challenge.book.net.json;

public enum DiffOrderState {

	CANCELLED("cancelled"), OPEN("open");
	
	private final String state;
	
	private DiffOrderState(String state) {
		this.state = state;
	}
	
	public static DiffOrderState getBy(String state) {
		for (DiffOrderState doState : values()) {
			if (doState.state.equals(state))
				return doState;
		}
		
		throw new IllegalArgumentException("State does not match mapped states");
	}
}
