package org.sonar.challenge.trades;

import static org.sonar.challenge.main.GlobalPropertiesConfig.DECIMAL_FORMAT;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.sonar.challenge.book.Trade;
import org.sonar.challenge.main.GlobalPropertiesConfig;
import org.sonar.challenge.main.LimitObserver;
import org.sonar.challenge.order.DoNothingOrderIssuerImpl;
import org.sonar.challenge.order.OrderBatch;
import org.sonar.challenge.strategy.DefaultTradingStrategyFactory;
import org.sonar.challenge.strategy.TradingStrategyFactory;
import org.sonar.challenge.trade.TradingEngineImpl;
import org.sonar.challenge.trade.TradingEngineImplBuilder;
import org.sonar.challenge.trade.TradingEngineListener;
import org.sonar.challenge.trade.TradingEngineManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class TradesController implements Initializable, LimitObserver, TradingEngineListener {

	@FXML
	private TableView<Trade> tradesTbl;

	@FXML
	private Label tradesLbl;

	@FXML
	private TableColumn<Trade, BigDecimal> itemPriceCol;

	@FXML
	private TableColumn<Trade, BigDecimal> itemAmountCol;

	private ObservableList<Trade> data;

	private final String name;

	private int limitTradesToDisplay;

	private TradingEngineImpl tradingEngine = null;

	public TradesController() {
		this("");
	}

	TradesController(String name) {
		this.name = name;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		itemPriceCol.setCellValueFactory(new PropertyValueFactory<Trade, BigDecimal>("price"));
		itemPriceCol.setCellFactory(col -> createBigDecimalFormatterCell());
		itemAmountCol.setCellValueFactory(new PropertyValueFactory<Trade, BigDecimal>("amount"));
		itemAmountCol.setCellFactory(col -> createBigDecimalFormatterCell());

		data = FXCollections.observableArrayList();
		tradesTbl.setItems(data);

		setLabels(name);

		GlobalPropertiesConfig.getInstance().addLimitTradesObserver(this);
		initTradesEngine();
	}

	@Override
	public void update(int limit) {
		this.limitTradesToDisplay = limit;
		initTradesEngine();
	}

	@Override
	public void onTradeListChange(List<Trade> oldList, List<Trade> newTradeList) {
		synchronized (data) {
			if (newTradeList != null && !newTradeList.equals(oldList)) {
				data.clear();
				data.addAll(newTradeList);
			}
		}
	}

	void setLabels(String name) {
		tradesLbl.setText(name);
	}

	private void initTradesEngine() {
		// Build the tradingEngine.
		tradingEngine = new TradingEngineImplBuilder().limit(limitTradesToDisplay)
				.bookName(GlobalPropertiesConfig.getInstance().getBookName()).inherateStateFrom(tradingEngine).build();
		tradingEngine.addListener(this);
		tradingEngine.addOrderIssuer(new DoNothingOrderIssuerImpl());
		// TODO Alvaro: Add the specific trading strategy
		tradingEngine.addTradingStrategyFactory(new DefaultTradingStrategyFactory());

		GlobalPropertiesConfig.getInstance().getTradingEngineManager().execute(tradingEngine);
	}

	private TableCell<Trade, BigDecimal> createBigDecimalFormatterCell() {
		TableCell<Trade, BigDecimal> cellBigDecimal = new TableCell<Trade, BigDecimal>() {
			protected void updateItem(BigDecimal item, boolean empty) {
				super.updateItem(item, empty);
				addPersonalStyleCell(this);
				setText(empty ? "" : DECIMAL_FORMAT.format(item));
			}
		};
		return cellBigDecimal;
	}

	private <T> TableCell<Trade, T> addPersonalStyleCell(final TableCell<Trade, T> oldCell) {
		if (oldCell != null && oldCell.getIndex() >= 0 && data.size() > oldCell.getIndex()) {
			Trade trade = data.get(oldCell.getIndex());
			if (!trade.getTradeId().isPresent()) {
				oldCell.setStyle("-fx-background-color: green;");
			}
		}

		return oldCell;
	}

}
