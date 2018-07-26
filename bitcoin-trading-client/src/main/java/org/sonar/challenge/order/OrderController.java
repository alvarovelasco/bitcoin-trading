package org.sonar.challenge.order;


import static org.sonar.challenge.main.GlobalPropertiesConfig.DECIMAL_FORMAT;
import static org.sonar.challenge.main.GlobalPropertiesConfig.FORMATTER;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.sonar.challenge.book.Order;
import org.sonar.challenge.main.GlobalPropertiesConfig;

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

public class OrderController implements Initializable, LimitOrderObserver {

	@FXML
	private TableView<Order> ordersTbl;
	
	@FXML
	private Label ordersLbl;

	@FXML
	private TableColumn<Order, BigDecimal> itemPriceCol;

	@FXML
	private TableColumn<Order, BigDecimal> itemAmountCol;

	@FXML
	private TableColumn<Order, LocalDateTime> itemTimeCol;

	private ObservableList<Order> data;
	
	private final String name;
	
	private int limitOrdersToDisplay;
	
	public OrderController() {
		this("");
	}
	
	OrderController(String name) {
		this.name = name;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		itemPriceCol.setCellValueFactory(new PropertyValueFactory<Order, BigDecimal>("price"));
		itemPriceCol.setCellFactory(col -> createBigDecimalFormatterCell());
		itemAmountCol.setCellValueFactory(new PropertyValueFactory<Order, BigDecimal>("amount"));
		itemAmountCol.setCellFactory(col -> createBigDecimalFormatterCell());
		itemTimeCol.setCellValueFactory(new PropertyValueFactory<Order, LocalDateTime>("timestamp"));
		itemTimeCol.setCellFactory(col ->  new TableCell<Order, LocalDateTime>() {
		    @Override
		    protected void updateItem(LocalDateTime item, boolean empty) {

		        super.updateItem(item, empty);
		        if (empty)
		            setText(null);
		        else
		            setText(String.format(item.format(FORMATTER)));
		    }
		});

		data = FXCollections.observableArrayList();
		ordersTbl.setItems(data);
		
		setLabels(name);
	}

	@Override
	public void update(int limit) {
		this.limitOrdersToDisplay = limit;
	}
	
	void setLabels(String name) {
		ordersLbl.setText(name);
	}
	
	Parent getParentRoot() {
		return ordersTbl.getParent();
	}
	
	void updateWithNewData(List<Order> newOrders) {
		List<Order> newList = new ArrayList<>(data);
		newList.addAll(newOrders);
		
		newList = newList.stream().sorted(new Comparator<Order>() {
			@Override
			public int compare(Order o1, Order o2) {
				BigDecimal order1Value = o1.getAmount().multiply(o1.getPrice());
				BigDecimal order2Value = o2.getAmount().multiply(o2.getPrice()); 
				
				return order2Value.compareTo(order1Value);
			}
		}).limit(limitOrdersToDisplay).collect(Collectors.toList());
		
		data.clear();
		data.addAll(newList);
	}

	private TableCell<Order, BigDecimal> createBigDecimalFormatterCell() {
		TableCell<Order, BigDecimal> cellBigDecimal = new TableCell<Order, BigDecimal>() {
			protected void updateItem(BigDecimal item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : DECIMAL_FORMAT.format(item));
			}
		};
		return cellBigDecimal;
	}
}
