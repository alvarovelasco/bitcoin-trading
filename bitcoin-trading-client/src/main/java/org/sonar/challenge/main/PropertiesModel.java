package org.sonar.challenge.main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PropertiesModel {

	private final StringProperty bookName = new SimpleStringProperty();
	private final IntegerProperty orders = new SimpleIntegerProperty();
	private final IntegerProperty trades = new SimpleIntegerProperty();
	private final IntegerProperty downticks = new SimpleIntegerProperty();
	private final IntegerProperty upticks = new SimpleIntegerProperty();

	public PropertiesModel(String bookName, int orders, int trades, int downticks, int upticks) {
		this.bookName.set(bookName);
		this.orders.set(orders);
		this.trades.set(trades);
		this.downticks.set(downticks);
		this.upticks.set(upticks);
	}

	public StringProperty bookName() {
		return bookName;
	}

	public IntegerProperty orders() {
		return orders;
	}

	public IntegerProperty trades() {
		return trades;
	}
	
	public IntegerProperty upticks() {
		return upticks;
	}
	
	public IntegerProperty downticks() {
		return downticks;
	}

	public String getBookName() {
		return bookName.get();
	}
	
	public int getTrades() {
		return trades.get();
	}

	public int getOrders() {
		return orders.get();
	}

	public int getUpticks() {
		return upticks.get();
	}
	
	public int getDownticks() {
		return downticks.get();
	}
	
	@Override
	public String toString() {
		return "PropertiesModel [bookName=" + bookName + ", orders=" + orders + ", trades=" + trades + ", downticks="
				+ downticks + ", upticks=" + upticks + "]";
	}

	
}