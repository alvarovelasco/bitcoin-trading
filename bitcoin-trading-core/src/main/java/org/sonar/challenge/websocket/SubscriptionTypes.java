package org.sonar.challenge.websocket;

public enum SubscriptionTypes {
	DIFF_ORDERS("diff-orders");
	private final String keyword;

	private SubscriptionTypes(String key) {
		this.keyword = key;
	}
	
	public String getKeyword() {
		return keyword;
	}
}
