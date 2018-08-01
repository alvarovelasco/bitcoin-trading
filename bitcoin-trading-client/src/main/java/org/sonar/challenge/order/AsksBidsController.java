package org.sonar.challenge.order;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.sonar.challenge.order.Order;
import org.sonar.challenge.book.UpdatedOrderBook;
import org.sonar.challenge.book.subscription.SubscribeFeeder;
import org.sonar.challenge.book.subscription.difford.DiffOrderOnBookSubscribeFeederImpl;
import org.sonar.challenge.main.GlobalPropertiesConfig;
import org.sonar.challenge.main.LimitObserver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class AsksBidsController implements Initializable, LimitObserver {

	private OrderController bidsController;

	private OrderController asksController;

	@FXML
	private HBox hboxAsksBids;

	private boolean flagInitializedData = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			initControllers(resources);
			initSubscriptionEngine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initSubscriptionEngine() {
		SubscribeFeeder<UpdatedOrderBook> subscribeFeeder = new DiffOrderOnBookSubscribeFeederImpl(
				GlobalPropertiesConfig.getInstance().getBookName());
		subscribeFeeder.subscribe(u -> refresh(u));
		Platform.runLater(() -> subscribeFeeder.startFeeding());
	}

	private void initControllers(ResourceBundle resources) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(OrderController.class.getResource("Order.fxml"));
		AnchorPane bids = fxmlLoader.load();
		this.bidsController = fxmlLoader.getController();
		this.bidsController.setLabels(resources.getString("orderTbl.nameLbl.bids"));

		fxmlLoader = new FXMLLoader(OrderController.class.getResource("Order.fxml"));
		AnchorPane asks = fxmlLoader.load();
		this.asksController = fxmlLoader.getController();
		this.asksController.setLabels(resources.getString("orderTbl.nameLbl.asks"));
		
		hboxAsksBids.getChildren().add(bids);
		hboxAsksBids.getChildren().add(new SplitPane());
		hboxAsksBids.getChildren().add(asks);

		HBox.setHgrow(bids, Priority.ALWAYS);
		HBox.setHgrow(asks, Priority.ALWAYS);
		
		GlobalPropertiesConfig.getInstance().addLimitOrderObserver(this);
	}

	private void refresh(UpdatedOrderBook updatedOrderBook) {
		List<Order> bids = flagInitializedData ? updatedOrderBook.getNewBids()
				: updatedOrderBook.getUpToDateOrderBook().getBids();
		List<Order> asks = flagInitializedData ? updatedOrderBook.getNewAsks()
				: updatedOrderBook.getUpToDateOrderBook().getAsks();

		Platform.runLater(() -> {
			this.bidsController.updateWithNewData(bids);
			this.asksController.updateWithNewData(asks);
		});

		flagInitializedData = true;
	}
	
	@Override
	public void update(int limit) {
		flagInitializedData = false;
	}
}
