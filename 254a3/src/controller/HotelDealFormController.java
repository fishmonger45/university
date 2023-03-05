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
import model.HotelDeal;
import model.MainModel;

public class HotelDealFormController {

	@FXML
	TextField textFieldHotel;

	@FXML
	TextField textFieldDuration;

	@FXML
	TextField textFieldPrice;

	@FXML
	Label labelError;

	private MainModel model;

	public void initialize() {
		this.model = MainModel.getMainModel();
		// Populate
		textFieldHotel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldHotel.setStyle("");
			}
		});

		textFieldDuration.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				textFieldDuration.setStyle("");
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
		if (this.textFieldHotel.getText().equals("")) {
			this.textFieldHotel.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
			this.labelError.setVisible(true);
			this.labelError.setText("Error, please fill out the highlighted fields");
			flag = true;
		}
		if (this.textFieldDuration.getText().equals("")) {
			this.textFieldDuration.setStyle("-fx-border-color:#FF0000;-fx-border-width:2px;");
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
			this.model.hotelDeals.add(new HotelDeal(this.textFieldHotel.getText(), this.textFieldDuration.getText(),
					this.textFieldPrice.getText()));
			((Stage) this.textFieldHotel.getScene().getWindow()).close();
		}
	}

	public void onClickButtonCancel(Event e) {
		((Stage) this.textFieldHotel.getScene().getWindow()).close();
	}
}
