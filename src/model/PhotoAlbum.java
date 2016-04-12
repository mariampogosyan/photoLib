package model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that launches the PhotoAlbum application.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 *
 */
public class PhotoAlbum extends Application {
	@Override
	public void start(Stage stage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Login");
		stage.setResizable(false);  
		stage.show();
	}
	public static void main(String[] sa) {
		launch(sa);
	}	
}