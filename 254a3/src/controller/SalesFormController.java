package controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Customer;
import model.MainModel;
import model.Sale;

public class SalesFormController {

	@FXML
	ChoiceBox<String> choiceBoxProduct;

	@FXML
	TextField textFieldEmployee;

	@FXML
	ListView<String> listViewEmployee;

	@FXML
	TextField textFieldCustomer;

	@FXML
	ListView<String> listViewCustomer;

	@FXML
	Label labelError;

	@FXML
	DatePicker datePicker;

	MainModel model;

	public void initialize() {
		this.model = MainModel.getMainModel();
		choiceBoxProduct.getItems().add("Hotel Deal");
		choiceBoxProduct.getItems().add("Package Deal");
		choiceBoxProduct.getItems().add("Flight");
		choiceBoxProduct.getItems().add("Product Special");
		choiceBoxProduct.setValue("Hotel Deal");
		// Populate form with sale data
		if (model.type.equals(MainModel.TYPE.EDIT)) {
			this.textFieldCustomer.setText(this.model.sales.get(this.model.currentlySelected).customer);
			this.textFieldEmployee.setText(this.model.sales.get(this.model.currentlySelected).employee);
			this.datePicker.getEditor().setText(this.model.sales.get(this.model.currentlySelected).date);
		} else {
			this.model.employees.stream().forEach(x -> {
				listViewEmployee.getItems().add(x);
			});

			this.model.customers.stream().forEach(x -> {
				listViewCustomer.getItems().add(x.name);
			});
		}

		this.textFieldEmployee.textProperty().addListener((observable, oldValue, newValue) -> {
			listViewEmployee.getItems().clear();
			this.model.employees.stream().forEach(x -> {
				if (x.contains(newValue)) {
					listViewEmployee.getItems().add(x);
				}
			});
		});

		this.textFieldCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
			listViewCustomer.getItems().clear();
			this.model.customers.stream().forEach(x -> {
				if (x.name.contains(newValue)) {
					listViewCustomer.getItems().add(x.name);
				}
			});
		});

		// Double click list item Employee
		listViewEmployee.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				textFieldEmployee.setStyle("");
				textFieldEmployee.setText(listViewEmployee.getSelectionModel().getSelectedItem());
			}
		});

		// Double click list item Customer
		listViewCustomer.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				textFieldCustomer.setStyle("");
				textFieldCustomer.setText(listViewCustomer.getSelectionModel().getSelectedItem());
			}
		});

		textFieldEmployee.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldEmployee.setStyle("");
			}
		});

		textFieldCustomer.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldCustomer.setStyle("");
			}
		});

		datePicker.getEditor().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				datePicker.setStyle("");
			}
		});

		datePicker.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				datePicker.setStyle("");
			}
		});
	}

	@FXML
	private void onClickButtonOk(Event e) {

		// flag true if error
		boolean flag = false;
		boolean flag2 = false;
		// Check valid Employee
		for (Customer cust : this.model.customers) {
			if (cust.name.contains(this.textFieldCustomer.getText())) {
				flag2 = true;
			}
		}
		if (!flag2) {
			this.textFieldCustomer.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (!model.employees.contains(this.textFieldEmployee.getText())) {
			this.textFieldEmployee.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.datePicker.getEditor().getText().equals("")) {
			this.datePicker.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setText("Error, please fill out the highlighted fields");
			this.labelError.setVisible(true);
			flag = true;
		}

		// Accept
		if (!flag) {
			// Add to model
			if (model.type.equals(MainModel.TYPE.NEW)) {
				this.model.sales.add(new Sale(this.choiceBoxProduct.getValue(), this.textFieldCustomer.getText(),
						this.textFieldEmployee.getText(), this.datePicker.getEditor().getText()));
			} else { // change model
				this.model.sales.set(this.model.currentlySelected,
						(new Sale(this.choiceBoxProduct.getValue(), this.textFieldCustomer.getText(),
								this.textFieldEmployee.getText(), this.datePicker.getEditor().getText())));

			}
			((Stage) this.choiceBoxProduct.getScene().getWindow()).close();
		}

	}

	@FXML
	private void onClickButtonCancel(Event e) {
		((Stage) this.choiceBoxProduct.getScene().getWindow()).close();
	}

}
