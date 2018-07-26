package org.sonar.challenge.main;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.sonar.challenge.order.LimitOrderObserver;

public class GlobalPropertiesConfig {

	private static final GlobalPropertiesConfig INSTANCE = new GlobalPropertiesConfig();

	public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
	
	public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.0000");
	
	private final static String BTC_MXN = "btc_mxn";
	
	private int limitOrdersToDisplay = 25;
	
	private int tradesToDisplay = 25;
	
	private String bookName = BTC_MXN;
	
	private List<LimitOrderObserver> limitOrderObservers = new ArrayList<>();
	
	private GlobalPropertiesConfig() {
	}
	
	public static GlobalPropertiesConfig getInstance() {
		return INSTANCE;
	}
	
	public void addLimitOrderObserver(LimitOrderObserver limitOrderObserver) {
		limitOrderObservers.add(limitOrderObserver);
		limitOrderObserver.update(limitOrdersToDisplay);
	}
	
	public void setLimitOrdersToDisplay(int limitOrdersToDisplay) {
		this.limitOrdersToDisplay = limitOrdersToDisplay;
		limitOrderObservers.stream().forEach(o -> o.update(limitOrdersToDisplay));
	}
	
	public int getLimitOrdersToDisplay() {
		return limitOrdersToDisplay;
	}
	
	private void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public int getTradesToDisplay() {
		return tradesToDisplay;
	}
	
	public void setTradesToDisplay(int tradesToDisplay) {
		this.tradesToDisplay = tradesToDisplay;
	}
	
	public void update(PropertiesModel propertiesModel) {
		Objects.requireNonNull(propertiesModel);
		
		setBookName(propertiesModel.getBookName()); 
		setLimitOrdersToDisplay(propertiesModel.getOrders());
		setTradesToDisplay(propertiesModel.getTrades());
	}
	
}
