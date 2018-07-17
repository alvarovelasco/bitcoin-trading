package org.sonar.challenge.book.net.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DiffOrderDecoder {
			  
	private String type;

	private String book;
	
	private String sequence;
	
	private List<PayloadOrder> payload;
	
	public String getBook() {
		return book;
	}
	
	public List<PayloadOrder> getPayload() {
		return new ArrayList<>(payload);
	}
	
	public long getSequence() {
		return Long.parseLong(sequence);
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "DiffOrderDecoder [type=" + type + ", book=" + book + ", sequence=" + sequence + ", payload=" + payload
				+ "]";
	}

	static class PayloadOrder {
		@SerializedName("d")
		private long timestamp;
		
		@SerializedName("t")
		private int type;
		
		@SerializedName("a")
		private BigDecimal amount;
		
		@SerializedName("v")
		private BigDecimal value;
		
		@SerializedName("s")
		private String state;
		
		public BigDecimal getAmount() {
			return amount;
		}
		
		public long getTimestamp() {
			return timestamp;
		}
		
		public int getType() {
			return type;
		}
		
		public BigDecimal getValue() {
			return value;
		}
		
		public DiffOrderState getState() {
			return DiffOrderState.getBy(state);
		}
	}
}
