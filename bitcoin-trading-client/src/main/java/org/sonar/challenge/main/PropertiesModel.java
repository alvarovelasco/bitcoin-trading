package org.sonar.challenge.main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PropertiesModel {

	private final StringProperty bookName = new SimpleStringProperty();
	private final IntegerProperty orders = new SimpleIntegerProperty();
	private final IntegerProperty trades = new SimpleIntegerProperty();;

	public PropertiesModel(String bookName, int orders, int trades) {
		this.bookName.set(bookName);
		this.orders.set(orders);
		this.trades.set(trades);
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

	public String getBookName() {
		return bookName.get();
	}

	public int getTrades() {
		return trades.get();
	}

	public int getOrders() {
		return orders.get();
	}

	@Override
	public String toString() {
		return "PropertiesModel [bookName=" + bookName + ", orders=" + orders + ", trades=" + trades + "]";
	}

	
}