package org.sonar.challenge.book.net.json;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OrderBookDecoder {

	private boolean success;
	
	private Payload payload;
	
	public List<Order> getBids() {
		return new ArrayList<Order>( payload.bids);
	}
	
	public List<Order> getAsks() {
		return new ArrayList<Order>( payload.asks);
	}
	
	public String getSequence() {
		return payload.sequence;
	}
	
	public LocalDateTime getUpdateTime() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+'SS:SS");
		return LocalDateTime.parse(payload.updateTime, formatter);
	}

	private static class Payload {

		private String sequence;
		
		private List<Order> bids;
		
		private List<Order> asks;
		
		@SerializedName("updated_at")
		private String updateTime;
	
	}
	
	protected static class Order {
		private String book;
		
		private BigDecimal price;
		
		private BigDecimal amount;
		
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


