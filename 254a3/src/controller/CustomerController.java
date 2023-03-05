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
import model.Customer;
import model.MainModel;
import utilities.SceneManager;

public class CustomerController {

	MainModel model;

	@FXML
	ListView<String> listName;

	@FXML
	ListView<String> listEmail;
	@FXML
	ListView<String> listAddress;

	@FXML
	ListView<String> listContact;

	@FXML
	ListView<String> listCreditCard;

	public void initialize() {
		this.model = MainModel.getMainModel();
		for (Customer customer : this.model.customers) {
			listName.getItems().add(customer.name);
			listEmail.getItems().add(customer.email);
			listAddress.getItems().add(customer.address);
			listContact.getItems().add(customer.contactNumber);
			listCreditCard.getItems().add(customer.creditCardNumber);
		}

		listName.getSelectionModel().select(0);
		listEmail.getSelectionModel().select(0);
		listAddress.getSelectionModel().select(0);
		listContact.getSelectionModel().select(0);
		listCreditCard.getSelectionModel().select(0);

		listName.setOnMousePressed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				int index = listName.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listEmail.getSelectionModel().select(index);
				listAddress.getSelectionModel().select(index);
				listContact.getSelectionModel().select(index);
				listCreditCard.getSelectionModel().select(index);
			}

		});
		listEmail.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listEmail.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listName.getSelectionModel().select(index);
				listAddress.getSelectionModel().select(index);
				listContact.getSelectionModel().select(index);
				listCreditCard.getSelectionModel().select(index);
			}

		});
		listAddress.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listAddress.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listName.getSelectionModel().select(index);
				listEmail.getSelectionModel().select(index);
				listContact.getSelectionModel().select(index);
				listCreditCard.getSelectionModel().select(index);
			}

		});
		listContact.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listContact.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listName.getSelectionModel().select(index);
				listEmail.getSelectionModel().select(index);
				listAddress.getSelectionModel().select(index);
				listCreditCard.getSelectionModel().select(index);
			}

		});
		listCreditCard.setOnMousePressed(new EventHandler<Event>() {

			@Override
			public void handle(Event e) {
				int index = listCreditCard.getSelectionModel().getSelectedIndex();
				model.currentlySelected = index;
				listName.getSelectionModel().select(index);
				listEmail.getSelectionModel().select(index);
				listAddress.getSelectionModel().select(index);
				listContact.getSelectionModel().select(index);
			}

		});
	}

	@FXML
	private void onClickButtonBack(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/MainMenuView.fxml"), e);
	}

	@FXML
	private void onClickButtonEdit(Event event) {
		this.model.type = MainModel.TYPE.EDIT;
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"));
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
		listName.getItems().clear();
		listEmail.getItems().clear();
		listAddress.getItems().clear();
		listContact.getItems().clear();
		listCreditCard.getItems().clear();
		for (Customer customer: model.customers) {
			listName.getItems().add(customer.name);
			listEmail.getItems().add(customer.email);
			listAddress.getItems().add(customer.address);
			listContact.getItems().add(customer.contactNumber);
			listCreditCard.getItems().add(customer.creditCardNumber);
		}
	}

	@FXML
	private void onClickButtonNew(Event event) {
		this.model.type = MainModel.TYPE.NEW;
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"));
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
		listName.getItems().clear();
		listEmail.getItems().clear();
		listAddress.getItems().clear();
		listContact.getItems().clear();
		listCreditCard.getItems().clear();
		for (Customer customer: model.customers) {
			listName.getItems().add(customer.name);
			listEmail.getItems().add(customer.email);
			listAddress.getItems().add(customer.address);
			listContact.getItems().add(customer.contactNumber);
			listCreditCard.getItems().add(customer.creditCardNumber);
		}
	}
}
