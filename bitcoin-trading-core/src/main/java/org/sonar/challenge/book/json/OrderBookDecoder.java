package org.sonar.challenge.book.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class OrderBookDecoder {

	private boolean success;
	
	private Payload payload;
	
	OrderBookDecoder(Payload payload) {
		this.payload = payload;
	}
	
	public List<Order> getBids() {
		return new ArrayList<Order>( payload.bids);
	}
	
	public List<Order> getAsks() {
		return new ArrayList<Order>( payload.asks);
	}
	
	public String getSequence() {
		return payload.sequence;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public LocalDateTime getUpdateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME; 
		return LocalDateTime.parse(payload.updateTime, formatter);
	}

	static class Payload {

		private String sequence;
		
		private List<Order> bids;
		
		private List<Order> asks;
		
		@SerializedName("updated_at")
		private String updateTime;
		
		Payload(String sequence, List<Order> bids, List<Order> asks, String updatedTime) {
			this.sequence = Objects.requireNonNull(sequence);
			this.bids = Objects.requireNonNull(bids);
			this.asks = Objects.requireNonNull(asks);
			this.updateTime = Objects.requireNonNull(updatedTime);
		}
	
	}
	
	static class Order {
		private String book;
		
		private BigDecimal price;
		
		private BigDecimal amount;
		
		Order(String book, BigDecimal price, BigDecimal amount) {
			this.book = Objects.requireNonNull(book);
			this.price = Objects.requireNonNull(price);
			this.amount = Objects.requireNonNull(amount);
		}
		
		public BigDecimal getAmount() {
			return amount;
		}
		
		public String getBook() {
			return book;
		}
		
		public BigDecimal getPrice() {
			return price;
		}
	}
}


