package controller;

import java.util.regex.Pattern;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Customer;
import model.MainModel;

public class CustomerFormController {
	@FXML
	TextField textFieldName;

	@FXML
	TextField textFieldEmail;

	@FXML
	TextField textFieldAddress;

	@FXML
	TextField textFieldNumber;

	@FXML
	TextField textFieldCreditCard;

	@FXML
	Label labelError;

	private MainModel model;

	public void initialize() {
		this.model = MainModel.getMainModel();
		// Populate
		if (model.type.equals(MainModel.TYPE.EDIT)) {
			Customer cust = model.customers.get(this.model.currentlySelected);
			textFieldName.setText(cust.name);
			textFieldEmail.setText(cust.email);
			textFieldAddress.setText(cust.address);
			textFieldNumber.setText(cust.contactNumber);
			textFieldCreditCard.setText(cust.creditCardNumber);
		}

		textFieldName.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldName.setStyle("");
			}
		});

		textFieldEmail.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldEmail.setStyle("");
			}
		});

		textFieldAddress.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldAddress.setStyle("");
			}
		});

		textFieldNumber.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldNumber.setStyle("");
			}
		});

		textFieldCreditCard.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldCreditCard.setStyle("");
			}
		});
	}

	public void onClickButtonOk(Event e) {
		boolean flag = false;
		if (this.textFieldName.getText().isEmpty()) {
			this.textFieldName.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldEmail.getText().isEmpty()) {
			this.textFieldEmail.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldAddress.getText().isEmpty()) {
			this.textFieldAddress.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldNumber.getText().isEmpty()) {
			this.textFieldNumber.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldCreditCard.getText().isEmpty()) {
			this.textFieldCreditCard.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (!Pattern.compile(
				"^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$")
				.matcher(this.textFieldCreditCard.getText()).matches()) {
			this.textFieldCreditCard.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, Invalid credit card number");
			flag = true;
		}

		if (!flag) {
			// Edit
			if (this.model.type.equals(MainModel.TYPE.EDIT)) {
				this.model.customers.set(this.model.currentlySelected,
						new Customer(this.textFieldName.getText(), this.textFieldEmail.getText(),
								this.textFieldAddress.getText(), this.textFieldNumber.getText(),
								this.textFieldCreditCard.getText()));
			} else {
				this.model.customers.add(new Customer(this.textFieldName.getText(), this.textFieldEmail.getText(),
						this.textFieldAddress.getText(), this.textFieldNumber.getText(),
						this.textFieldCreditCard.getText()));
			}
			((Stage) this.textFieldAddress.getScene().getWindow()).close();
		}
	}

	public void onClickButtonCancel(Event e) {
		((Stage) this.textFieldAddress.getScene().getWindow()).close();
	}
}
