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
import model.Flight;
import model.MainModel;

public class FlightFormController {

	@FXML
	TextField textFieldDepature;

	@FXML
	TextField textFieldDestination;

	@FXML
	TextField textFieldPrice;

	@FXML
	Label labelError;

	private MainModel model;

	public void initialize() {
		this.model = MainModel.getMainModel();
		// Populate
		textFieldDepature.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldDepature.setStyle("");
			}
		});

		textFieldDestination.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldDestination.setStyle("");
			}
		});

		textFieldPrice.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldPrice.setStyle("");
			}
		});
	}

	public void onClickButtonOk(Event e) {
		boolean flag = false;
		if (this.textFieldDepature.getText().equals("")) {
			this.textFieldDepature.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldDestination.getText().equals("")) {
			this.textFieldDestination.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldPrice.getText().equals("")) {
			this.textFieldPrice.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}

		if (!flag) {
			this.model.flights.add(new Flight(this.textFieldDepature.getText(), this.textFieldDestination.getText(),
					this.textFieldPrice.getText()));
			((Stage) this.textFieldDepature.getScene().getWindow()).close();
		}
	}

	public void onClickButtonCancel(Event e) {
		((Stage) this.textFieldDepature.getScene().getWindow()).close();
	}
}
