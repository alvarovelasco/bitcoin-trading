package org.sonar.challenge.order;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * Contains the representation of a specific order (this may be a sell or buy)
 * 
 * @author Alvaro
 *
 */
public final class Order  {

	private final BigDecimal price;
	
	private final BigDecimal amount;
	
	private final LocalDateTime at;
	
	private final OrderType type;
	
	public Order(BigDecimal price, BigDecimal amount, OrderType type , LocalDateTime at) {
		this.price = requireNonNull(price);
		this.amount = requireNonNull(amount);
		this.type = requireNonNull(type);
		this.at = at;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public OrderType getType() {
		return type;
	}
	
	public LocalDateTime getTimestamp() {
		return at;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((at == null) ? 0 : at.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Order other = (Order) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (at == null) {
			if (other.at != null)
				return false;
		} else if (!at.equals(other.at))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [price=" + price + ", amount=" + amount + ", at=" + at + "]";
	}

}
