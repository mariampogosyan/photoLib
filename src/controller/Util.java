package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Admin;

/**
 * Controller to handle displaying alerts, displaying errors, and changing screens
 * @author Stephen Dacayanan, Mariam Pogosyan
 *
 */

public class Util {
	/**
	 * Shows error alert
	 * @param errMessage
	 * @throws IOException
	 */
	public static void showErr(String errMessage) throws IOException {
		Alert errAlert = new Alert(AlertType.ERROR);
		errAlert.setHeaderText("Error");
		errAlert.setContentText(errMessage);
		errAlert.show();
	}
	/**
	 * Changes screen upon ActionEvent
	 * @param event
	 * @param name
	 * @param parent
	 * @param admin
	 * @throws Exception
	 */
	public static void changeScreen(ActionEvent event, String name, Parent parent, Admin admin) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Scene scene = new Scene(parent);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle(name);      
		stage.show();
	}
	/**
	 * Changes screen upon MouseEvent
	 * @param event
	 * @param name
	 * @param parent
	 * @param admin
	 * @throws Exception
	 */
	public static void changeScreen(MouseEvent event, String name, Parent parent, Admin admin) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide();
		Scene scene = new Scene(parent);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setResizable(false);
	
		stage.setScene(scene);
		stage.setTitle(name);
		stage.show();
	}
	/**
	 * Displays alerts
	 * @param message
	 * @param s
	 * @return
	 */
	public static Alert alert(String message, String s) {
		Alert alertBox = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
		alertBox.setContentText(s);
		alertBox.showAndWait();
		System.out.println();
		return alertBox;	

	}
}
