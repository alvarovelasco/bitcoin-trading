package org.sonar.challenge.websocket;

public enum SubscriptionTypes {
	// All the other types are meaningless at this stage of development.
	DIFF_ORDERS("diff-orders");
	private final String keyword;

	private SubscriptionTypes(String key) {
		this.keyword = key;
	}
	
	public String getKeyword() {
		return keyword;
	}
}
