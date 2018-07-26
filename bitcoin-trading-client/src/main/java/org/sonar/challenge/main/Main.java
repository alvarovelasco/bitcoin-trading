package org.sonar.challenge.main;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;

	private Parent rootLayout;

	private final static ResourceBundle MAIN_RESOURCES_BUNDLE = ResourceBundle
			.getBundle("org.sonar.challenge.order.bundle");

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(GlobalPropertiesConfig.getInstance().getBookName());

		initRootLayout();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void initRootLayout() {
		try {
			BorderPane root = new BorderPane();
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("../order/AsksBids.fxml"));
			loader.setResources(MAIN_RESOURCES_BUNDLE);
			rootLayout = loader.load();
			
			root.setTop(getMenuBar());
			root.setCenter(rootLayout);

			// Show the scene containing the root layout.
			Scene scene = new Scene(root);
			scene.getStylesheets().add("style1.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
