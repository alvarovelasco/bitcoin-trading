package org.sonar.challenge.main;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.sonar.challenge.order.LimitOrderObserver;

public class PropertiesConfig {

	private static final PropertiesConfig INSTANCE = new PropertiesConfig();

	public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
	
	public final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
	
	private final static String BTC_MXN = "btc_mxn";
	
	private int limitOrdersToDisplay = 25;
	
	private String bookName = BTC_MXN;
	
	private List<LimitOrderObserver> limitOrderObservers = new ArrayList<>();
	
	private PropertiesConfig() {
	}
	
	public static PropertiesConfig getInstance() {
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
	
	public String getBookName() {
		return bookName;
	}
	
}
