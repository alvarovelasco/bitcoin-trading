package org.sonar.challenge.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

public class PropertiesDialogController implements Initializable {

	@FXML
	private TextField bookName;
	
	@FXML
	private TextField orders2Display;
	
	@FXML
	private TextField trades2Display;
	
	@FXML
	private TextField upticks;
	
	@FXML
	private TextField downticks;
	
	private PropertiesModel propertiesModel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initModel();
	}

	private void initModel() {
		String book = GlobalPropertiesConfig.getInstance().getBookName();
		int orders = GlobalPropertiesConfig.getInstance().getLimitPositionsToDisplay();
		int trades = GlobalPropertiesConfig.getInstance().getTradesToDisplay();
		int upticks = GlobalPropertiesConfig.getInstance().getUpticks();
		int downticks = GlobalPropertiesConfig.getInstance().getDownticks();
		
		propertiesModel = new PropertiesModel(book, orders, trades, downticks, upticks);
		bookName.textProperty().bind(propertiesModel.bookName());
		orders2Display.textProperty().bindBidirectional(propertiesModel.orders(), new NumberStringConverter());
		trades2Display.textProperty().bindBidirectional(propertiesModel.trades(), new NumberStringConverter());
		this.upticks.textProperty().bindBidirectional(propertiesModel.upticks(), new NumberStringConverter());
		this.downticks.textProperty().bindBidirectional(propertiesModel.downticks(), new NumberStringConverter());
	}

	public static Dialog<PropertiesModel> getNewDialog(ResourceBundle resources) {
		Dialog<PropertiesModel> dialog = new Dialog<>();

		try {
			final FXMLLoader loader = new FXMLLoader(Main.class.getResource("Properties.fxml"), resources);
			DialogPane dialogPane = (DialogPane) loader.load();
			
			dialog.setTitle(resources.getString("properties.title"));
			dialog.setResultConverter(new Callback<ButtonType, PropertiesModel>() {

				@Override
				public PropertiesModel call(ButtonType param) {					
					if (ButtonType.CANCEL.equals(param)) {
						return null;
					}

					return ((PropertiesDialogController) loader.getController()).propertiesModel;
				}
			});
			dialog.setDialogPane(dialogPane);
			dialog.setResizable(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dialog;
	}

}
