
package controller;


import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.Admin;
import model.User;

/**
 * Controller for Login screen. Users/admin login using this screen and are taken to their albums/admin screen.
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see Admin, User
 */

public class loginController implements Initializable {
	@FXML
	Button loginButton;
	@FXML
	private TextField user;
	Admin admin = Admin.getInstance("admin");
	private ArrayList<User> users;
	public  User af = null;
	public static int index;
	
	/**
	 * Logs in user if they are in the list of users. Logs in admin as well.
	 * @param event
	 * @throws Exception
	 */
	@FXML
	protected void login(ActionEvent event) throws Exception {
	
		if (user.getText().equals(
				"admin") /* && password.getText().equals(user.getText()) */) {
			Parent p = FXMLLoader.load(getClass().getResource("/view/adminPage.fxml"));
			Util.changeScreen(event, "Admin", p, admin);
		} else {
			users = new ArrayList<User>();
			admin = Admin.remake();
			users = admin.getUsers();
			if (users.isEmpty()) {
					Util.showErr("There are no registered users!");
					return;
			}
			for (User u : users) {
				if (u.getName().equals(user.getText())) {
					af = Admin.makeuser(u);
					index = admin.getUsers().indexOf(u);
					Parent p = FXMLLoader.load(getClass().getResource("/view/albumList.fxml"));
					String name = "Albums of " + u.getName();
					Util.changeScreen(event, name, p, admin);
					return;
				} 
			}
			Util.showErr("This user doesn't exist!Try again");
		}
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
