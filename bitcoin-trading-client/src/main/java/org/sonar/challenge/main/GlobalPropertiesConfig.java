package org.sonar.challenge.main;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sonar.challenge.trade.TradingEngineManager;

public class GlobalPropertiesConfig {

	private static final GlobalPropertiesConfig INSTANCE = new GlobalPropertiesConfig();

	public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
	
	public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.0000");
	
	private final TradingEngineManager tradingEngineManager = new TradingEngineManager(); 
	
	private final static String BTC_MXN = "btc_mxn";
	
	private int limitPositionsToDisplay = 60;
	
	private int tradesToDisplay = 25;
	
	private int downticks = 5;
	
	private int upticks = 5;
	
	private String bookName = BTC_MXN;
	
	private List<LimitObserver> limitPositionObservers = new ArrayList<>();

	private List<LimitObserver> limitTradesObservers = new ArrayList<>();
	
	private List<LimitObserver> limitUpticksObservers = new ArrayList<>();

	private List<LimitObserver> limitDownticksObservers = new ArrayList<>();
	
	private GlobalPropertiesConfig() {
	}
	
	public static GlobalPropertiesConfig getInstance() {
		return INSTANCE;
	}
	
	/**
	 * @param limitObserver position observer of changes on the number of positions (asks, bids) to display)
	 */
	public void addLimitPositionsObserver(LimitObserver limitObserver) {
		limitPositionObservers.add(limitObserver);
		limitObserver.update(limitPositionsToDisplay);
	}
	
	public void setLimitPositionsToDisplay(int limitPositionsToDisplay) {
		this.limitPositionsToDisplay = limitPositionsToDisplay;
		limitPositionObservers.stream().forEach(o -> o.update(limitPositionsToDisplay));
	}
	
	/**
	 * @return Positions to display (bids, asks)
	 */
	public int getLimitPositionsToDisplay() {
		return limitPositionsToDisplay;
	}
	
	public void addLimitDownticksObserver(LimitObserver limitObserver) {
		limitDownticksObservers.add(limitObserver);
		limitObserver.update(downticks);
	}
	
	public void addLimitUpticksObserver(LimitObserver limitObserver) {
		limitUpticksObservers.add(limitObserver);
		limitObserver.update(upticks);
	}
	
	public void setDownticks(int downticks) {
		this.downticks = downticks;
		limitDownticksObservers.stream().forEach(o -> o.update(downticks));
	}
	
	public int getDownticks() {
		return downticks;
	}
	
	public void setUpticks(int upticks) {
		this.upticks = upticks;
		limitUpticksObservers.stream().forEach(o -> o.update(upticks));
	}
	
	public int getUpticks() {
		return upticks;
	}
	
	private void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public void addLimitTradesObserver(LimitObserver limitObserver) {
		limitTradesObservers.add(limitObserver);
		limitObserver.update(tradesToDisplay);
	}
	
	public int getTradesToDisplay() {
		return tradesToDisplay;
	}
	
	public void setTradesToDisplay(int tradesToDisplay) {
		this.tradesToDisplay = tradesToDisplay;
		limitTradesObservers.stream().forEach(o -> o.update(tradesToDisplay));
	}
	
	public TradingEngineManager getTradingEngineManager() {
		return tradingEngineManager;
	}
	
	public void update(PropertiesModel propertiesModel) {
		Objects.requireNonNull(propertiesModel);
		
		setBookName(propertiesModel.getBookName()); 
		setLimitPositionsToDisplay(propertiesModel.getOrders());
		setTradesToDisplay(propertiesModel.getTrades());
	}
	
}
