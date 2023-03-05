package controller;

import java.io.IOException;

import application.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.MainModel;
import model.Sale;
import utilities.SceneManager;

public class SalesController {

	MainModel model;

	@FXML
	ListView<String> listProduct;

	@FXML
	ListView<String> listCustomer;
	@FXML
	ListView<String> listEmployee;

	@FXML
	ListView<String> listDate;
	

	public void initialize() {
		this.model = MainModel.getMainModel();

		// Remove glow
		listProduct.setStyle("-fx-focus-color: transparent;-fx-background-color: inherit");
		listCustomer.setStyle("-fx-focus-color: transparent;-fx-background-color: inherit");
		listEmployee.setStyle("-fx-focus-color: transparent;-fx-background-color: inherit");
		listDate.setStyle("-fx-focus-color: transparent;-fx-background-color: inherit");

		// Populate
		for (Sale sale : this.model.sales) {
			listProduct.getItems().add(sale.product);
			listCustomer.getItems().add(sale.customer);
			listEmployee.getItems().add(sale.employee);
			listDate.getItems().add(sale.date);
		}

		// Set initial selection
		listProduct.getSelectionModel().select(0);
		listCustomer.getSelectionModel().select(0);
		listEmployee.getSelectionModel().select(0);
		listDate.getSelectionModel().select(0);

		listProduct.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				int index = listProduct.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listCustomer.getSelectionModel().select(index);
				listEmployee.getSelectionModel().select(index);
				listDate.getSelectionModel().select(index);
			}

		});
		listCustomer.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listCustomer.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listProduct.getSelectionModel().select(index);
				listEmployee.getSelectionModel().select(index);
				listDate.getSelectionModel().select(index);
			}

		});
		listEmployee.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listEmployee.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listProduct.getSelectionModel().select(index);
				listCustomer.getSelectionModel().select(index);
				listDate.getSelectionModel().select(index);
			}

		});
		listDate.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listDate.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listProduct.getSelectionModel().select(index);
				listCustomer.getSelectionModel().select(index);
				listEmployee.getSelectionModel().select(index);
			}

		});

	}

	@FXML
	private void onClickButtonBack(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/MainMenuView.fxml"), e);
	}

	@FXML
	private void onClickButtonNew(Event event) {
		this.model.type = MainModel.TYPE.NEW;
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/SalesForm.fxml"));
			Stage dialog = new Stage();
			dialog.setScene(new Scene(root));
			dialog.initStyle(StageStyle.TRANSPARENT);

			// Calculate the center position of the parent Stage
			double centerXPosition = window.getX() + window.getWidth() / 2d;
			double centerYPosition = window.getY() + window.getHeight() / 2d;
			// Relocate the pop-up Stage
			dialog.setOnShown(e -> {
				dialog.setX(centerXPosition - dialog.getWidth() / 2d);
				dialog.setY(centerYPosition - dialog.getHeight() / 2d);
			});
			dialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Repopulate
		listProduct.getItems().clear();
		listCustomer.getItems().clear();
		listEmployee.getItems().clear();
		listDate.getItems().clear();
		for (Sale sale : model.sales) {
			listProduct.getItems().add(sale.product);
			listCustomer.getItems().add(sale.customer);
			listEmployee.getItems().add(sale.employee);
			listDate.getItems().add(sale.date);
		}

	}

	@FXML
	private void onClickButtonEdit(Event event) {
		this.model.type = MainModel.TYPE.EDIT;
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/SalesForm.fxml"));
			Stage dialog = new Stage();
			dialog.setScene(new Scene(root));
			dialog.initStyle(StageStyle.TRANSPARENT);

			// Calculate the center position of the parent Stage
			double centerXPosition = window.getX() + window.getWidth() / 2d;
			double centerYPosition = window.getY() + window.getHeight() / 2d;
			// Hide the pop-up stage before being shown and relocate pop-up
			dialog.setOnShown(e -> {
				dialog.setX(centerXPosition - dialog.getWidth() / 2d);
				dialog.setY(centerYPosition - dialog.getHeight() / 2d);
			});
			dialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Repopulate
		listProduct.getItems().clear();
		listCustomer.getItems().clear();
		listEmployee.getItems().clear();
		listDate.getItems().clear();
		for (Sale sale : model.sales) {
			listProduct.getItems().add(sale.product);
			listCustomer.getItems().add(sale.customer);
			listEmployee.getItems().add(sale.employee);
			listDate.getItems().add(sale.date);
		}

	}

	@FXML
	private void onClickButtonDelete(Event e) {
		this.model.sales.remove(listProduct.getSelectionModel().getSelectedIndex());

		// Repopulate
		listProduct.getItems().clear();
		listCustomer.getItems().clear();
		listEmployee.getItems().clear();
		listDate.getItems().clear();
		for (Sale sale : model.sales) {
			listProduct.getItems().add(sale.product);
			listCustomer.getItems().add(sale.customer);
			listEmployee.getItems().add(sale.employee);
			listDate.getItems().add(sale.date);
		}

	}
}
