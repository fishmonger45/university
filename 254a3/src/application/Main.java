package application;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private static Stage primaryStage;
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Main.primaryStage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenuView.fxml"));
			Scene scene = new Scene(root, 1000, 800);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Stage getPrimaryStage() {
		return Main.primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
