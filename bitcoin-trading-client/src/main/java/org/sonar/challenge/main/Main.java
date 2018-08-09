package org.sonar.challenge.main;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.sonar.challenge.order.AsksBidsController;
import org.sonar.challenge.trades.TradesController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;

	private final static ResourceBundle MAIN_RESOURCES_BUNDLE = ResourceBundle.getBundle("org.sonar.challenge.bundle");

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(GlobalPropertiesConfig.getInstance().getBookName());
		this.primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void initRootLayout() {
		try {
			BorderPane root = new BorderPane();

			TabPane tabFolder = new TabPane();
			addTab(tabFolder, "Orders", AsksBidsController.class.getResource("AsksBids.fxml"));
			addTab(tabFolder, "Trades", TradesController.class.getResource("Trades.fxml"));

			root.setTop(getMenuBar());
			root.setCenter(tabFolder);

			// Show the scene containing the root layout.
			Scene scene = new Scene(root);
			scene.getStylesheets().add("style1.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addTab(TabPane tabFolder, String title, URL location) throws IOException {
		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader(location);
		loader.setResources(MAIN_RESOURCES_BUNDLE);
		Parent parentPane = loader.load();
		Tab tab = new Tab(title);
		tab.setClosable(false);
		tab.setContent(parentPane);
		tabFolder.getTabs().add(tab);
	}

	private MenuBar getMenuBar() {
		MenuBar menuBar = new MenuBar();

		Menu menu = new Menu(MAIN_RESOURCES_BUNDLE.getString("menubar.menu.window"));
		menuBar.getMenus().add(menu);

		MenuItem mi = new MenuItem(MAIN_RESOURCES_BUNDLE.getString("menubar.menu.props"));
		menu.getItems().add(mi);
		mi.setOnAction(e -> {
			try {
				openProperties();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		return menuBar;
	}

	private void openProperties() throws IOException {
		Dialog<PropertiesModel> dialog = PropertiesDialogController.getNewDialog(MAIN_RESOURCES_BUNDLE);

		Optional<PropertiesModel> result = dialog.showAndWait();
		if (result.isPresent()) {
			GlobalPropertiesConfig.getInstance().update(result.get());
		}
	}

}
