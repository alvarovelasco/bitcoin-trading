package org.sonar.challenge.strategy;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;
import org.sonar.challenge.book.Trade;
import org.sonar.challenge.book.Trade.TradeBuilder;

public class TradeTickResolverImplTest {
	
	@Test
	public void test_tickSamePrice() {
		TradeBuilder tBuilder = new Trade.TradeBuilder().amount(BigDecimal.ONE).price(BigDecimal.ONE);
		Trade t1 = tBuilder.build();
		Trade t2 = tBuilder.build();
		
		Optional<Ticks> tick = resolveTick(t1, t2);
		
		assertEquals(Optional.empty(), tick);
	}
	
	@Test
	public void test_tickMoreExpesiveThanPrevious() {
		TradeBuilder tBuilder = new Trade.TradeBuilder().amount(BigDecimal.ONE).price(BigDecimal.ONE);
		Trade t1 = tBuilder.build();
		Trade t2 = tBuilder.price(BigDecimal.TEN).build();
		
		Optional<Ticks> tick = resolveTick(t1, t2);
		
		assertEquals(Optional.of(Ticks.UPTICK), tick);
	}

	@Test
	public void test_tickCheaperThanPrevious() {
		TradeBuilder tBuilder = new Trade.TradeBuilder().amount(BigDecimal.ONE).price(BigDecimal.TEN);
		Trade t1 = tBuilder.build();
		Trade t2 = tBuilder.price(BigDecimal.ONE).build();
		
		Optional<Ticks> tick = resolveTick(t1, t2);
		
		assertEquals(Optional.of(Ticks.DOWNTICK), tick);
	}

	private Optional<Ticks> resolveTick(Trade t1, Trade t2) {
		TradeTickResolver tradeTickResolver = 
				new TradeTickResolverImpl();
		
		Optional<Ticks> tick = tradeTickResolver.resolve(t1, t2);
		return tick;
	}
	
}
