package controller;

import java.io.IOException;

import application.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Flight;
import model.HotelDeal;
import model.MainModel;
import utilities.SceneManager;

public class ProductController {
	
	@FXML
	ListView<String> listDepature;
	@FXML
	ListView<String> listDestination;
	@FXML
	ListView<String> listPrice;

	@FXML
	ListView<String> listHotel;
	@FXML
	ListView<String> listDuration;
	@FXML
	ListView<String> listPrice2;

	MainModel model;
	public void initialize() {

		this.model = MainModel.getMainModel();
		for(Flight flight : model.flights ) {
			listDepature.getItems().add(flight.depature);
			listDestination.getItems().add(flight.destination);
			listPrice.getItems().add(flight.price);
		}

		for(HotelDeal hotel : model.hotelDeals) {
			listHotel.getItems().add(hotel.hotel);
			listDuration.getItems().add(hotel.duration);
			listPrice2.getItems().add(hotel.price);
		}

	}

	@FXML
	private void onClickButtonFlightNew(Event event) {
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/FlightForm.fxml"));
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
		
		//Refresh
		listDepature.getItems().clear();
		listDestination.getItems().clear();
		listPrice.getItems().clear();
		for (Flight flight : model.flights) {
			listDepature.getItems().add(flight.depature);
			listDestination.getItems().add(flight.destination);
			listPrice.getItems().add(flight.price);
		}
	}

	@FXML
	private void onClickButtonHotelDealNew(Event event) {
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/HotelDealForm.fxml"));
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
		//Refresh
		listHotel.getItems().clear();
		listDuration.getItems().clear();
		listPrice2.getItems().clear();
		for (HotelDeal flight : model.hotelDeals) {
			listHotel.getItems().add(flight.hotel);
			listDuration.getItems().add(flight.duration);
			listPrice2.getItems().add(flight.price);
		}
	}
	
	@FXML
	private void onClickButtonBack(Event e) {
		SceneManager.changeScene(getClass().getResource("/view/MainMenuView.fxml"), e);
	}
}
