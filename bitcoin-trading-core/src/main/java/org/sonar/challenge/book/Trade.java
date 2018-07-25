package org.sonar.challenge.book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public final class Trade  {

	private final LocalDateTime timestamp;

	private final BigDecimal amount;

	private final BigDecimal price;

	private final Optional<Long> tradeId;
	
	private Trade(BigDecimal amount, BigDecimal price, LocalDateTime timestamp,Optional<Long> tradeId) {
		this.timestamp = timestamp;
		this.amount = amount;
		this.price = price;
		this.tradeId = tradeId;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public Optional<Long> getTradeId() {
		return tradeId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((tradeId == null) ? 0 : tradeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trade other = (Trade) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (tradeId == null) {
			if (other.tradeId != null)
				return false;
		} else if (!tradeId.equals(other.tradeId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Trade [timestamp=" + timestamp + ", amount=" + amount + ", price=" + price + ", tradeId="
				+ tradeId + "]";
	}

	public static class TradeBuilder {

		private BigDecimal amount;

		private BigDecimal price;

		private Optional<Long> tradeId = Optional.empty();
		
		private Optional<LocalDateTime> timestamp = Optional.empty();
		
		public TradeBuilder() {
		}
		
		public TradeBuilder price(BigDecimal price) {
			this.price = price;
			return this;
		}
		public TradeBuilder amount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}
		
		public TradeBuilder timestamp(LocalDateTime timestamp) {
			this.timestamp = Optional.of(timestamp);
			return this;
		}
		
		public TradeBuilder tradeId(long tradeId) {
			this.tradeId = Optional.of(tradeId);
			return this;
		}
		
		public Trade build() {
			if (amount == null) {
				throw new IllegalArgumentException("Amount not defined");
			}
			if (price == null) {
				throw new IllegalArgumentException("Price not defined");
			}

			return new Trade(amount, price, timestamp.orElse(LocalDateTime.now()), tradeId);
		}
	}
	
}