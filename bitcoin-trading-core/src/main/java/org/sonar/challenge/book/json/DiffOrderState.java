package org.sonar.challenge.book.json;

public enum DiffOrderState {

	CANCELLED("cancelled"), COMPLETED("completed"), OPEN("open");
	
	private final String state;
	
	private DiffOrderState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return state;
	}
	
	public static DiffOrderState getBy(String state) {
		for (DiffOrderState doState : values()) {
			if (doState.state.equals(state))
				return doState;
		}
		
		throw new IllegalArgumentException("State "+state+" does not match mapped states");
	}
}
