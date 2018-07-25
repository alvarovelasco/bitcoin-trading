package org.sonar.challenge.main;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private Parent rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");

		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("../order/AsksBids.fxml"));
			loader.setResources(ResourceBundle.getBundle("org.sonar.challenge.order.bundle"));
			rootLayout = loader.load();
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add("style1.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// // Load person overview.
	// FXMLLoader loader = new FXMLLoader();
	// loader.setLocation(Main.class.getResource("../order/OrderTab.fxml"));
	// GridPane personOverview = (GridPane) loader.load();
	//
	// // Set person overview into the center of root layout.
	// rootLayout.setCenter(personOverview);
	//
	// // Give the controller access to the main app.
}
