package controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.MainModel;
import utilities.SceneManager;

public class MainMenuController {

	MainModel model;

	@FXML
	private Button buttonQuit;

	public void initialize() {
		model = MainModel.getMainModel();
	}

	@FXML
	private void onClickButtonSales(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/SalesView.fxml"), e);
	}

	@FXML
	private void onClickButtonProducts(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/ProductView.fxml"), e);
	}

	@FXML
	private void onClickButtonCustomers(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/CustomerView.fxml"), e);
	}

	@FXML
	private void onClickButtonEmployees(Event e) {
	}

	@FXML
	private void onClickButtonQuit(Event e) {
		((Stage) this.buttonQuit.getScene().getWindow()).close();
		System.exit(0);
	}
}
