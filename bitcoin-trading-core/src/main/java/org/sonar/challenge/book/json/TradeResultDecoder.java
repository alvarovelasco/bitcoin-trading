package org.sonar.challenge.book.json;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class TradeResultDecoder {

	private boolean success;

	private List<Trade> payload;
	
	public List<Trade> getPayload() {
		return new ArrayList<>(payload);
	}

	public boolean isSuccess() {
		return success;
	}
	
	public static class Trade {
		private String book;

		@SerializedName("created_at")
		private String timestamp;

		private BigDecimal amount;

		@SerializedName("maker_side")
		private String makerSide;

		private BigDecimal price;

		@SerializedName("tid")
		private long tradeId;

		public BigDecimal getAmount() {
			return amount;
		}

		public String getBook() {
			return book;
		}

		public String getMakerSide() {
			return makerSide;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public LocalDateTime getTimestamp() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+'SSSS");
			return LocalDateTime.parse(timestamp, formatter);
		}

		public long getTradeId() {
			return tradeId;
		}
	}
}
