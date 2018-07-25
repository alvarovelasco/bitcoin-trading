package org.sonar.challenge.book.json;

import static java.util.Objects.requireNonNull;

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
	
	public void setBook(String book) {
		this.book = book;
	}
	
	public void setPayload(List<PayloadOrder> payload) {
		this.payload = payload;
	}
	
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "DiffOrderDecoder [type=" + type + ", book=" + book + ", sequence=" + sequence + ", payload=" + payload
				+ "]";
	}

	public static class PayloadOrder {
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
		
		public PayloadOrder(BigDecimal amount, BigDecimal value,DiffOrderMessageType diffOrderMessageType,
				DiffOrderState state, long timestamp) {
			this.amount = amount;
			this.value = value;
			this.state = requireNonNull(state).getState();
			this.type = requireNonNull(diffOrderMessageType).getNumber();
			this.timestamp = timestamp;
		}
		
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
