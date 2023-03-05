package utilities;

import java.io.IOException;
import java.net.URL;

import com.sun.glass.events.WindowEvent;

import application.Main;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SceneManager {

	public static void changeScene(URL resource, Event event) {
		try {
			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Double width = ((Node) event.getSource()).getScene().getWidth();
			Double height = ((Node) event.getSource()).getScene().getHeight();
			FXMLLoader loader = new FXMLLoader(resource);
			Parent parent = loader.load();
			Scene scene = new Scene(parent, width, height);
			window.setScene(scene);
			window.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addStage(URL resource, Event event) {
		Stage window = Main.getPrimaryStage();
		try {
			Parent root = FXMLLoader.load(resource);
			Stage dialog = new Stage();
			dialog.setScene(new Scene(root));
			dialog.initStyle(StageStyle.TRANSPARENT);
			// Calculate the center position of the parent Stage
			double centerXPosition = window.getX() + window.getWidth() / 2d;
			double centerYPosition = window.getY() + window.getHeight() / 2d;
			// Hide the pop-up stage before being shown and relocate pop-up
			dialog.setOnShowing(e -> dialog.hide());
			// Relocate the pop-up Stage
			dialog.setOnShown(e -> {
				dialog.setX(centerXPosition - dialog.getWidth() / 2d);
				dialog.setY(centerYPosition - dialog.getHeight() / 2d);
				dialog.show();
			});
			dialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
