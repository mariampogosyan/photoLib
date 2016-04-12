package controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import model.Admin;
import model.User;

/**
 * Controller for Admin List Screen. Admin can add users to have them be able to use the app by logging in with their user name.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see Admin
 *
 */

public class adminListController implements Serializable {
	private static final long serialVersionUID = 1L;
	@FXML Button adminLogOut;
	@FXML Button addUser;
	@FXML Button deleteUser;
	@FXML ListView<User> userList;
  	@FXML private ObservableList<User> users ;
  	Admin admin = Admin.getInstance("admin");
  	/**
  	 * Initializes the AdminList screen by loading in the list of current users in the program into a list. Admins can then add and delete users from the album.
  	 * @throws ClassNotFoundException
  	 * @throws IOException
  	 * @see Admin.getUsers
  	 */
  	@FXML
 	public void initialize() throws ClassNotFoundException, IOException {
  		load();
  		//admin = Admin.remake();
 		users = FXCollections.observableArrayList(admin.getUsers());
 		userList.setItems(users);
	    userList.getSelectionModel().select(0);
		userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>(){
			public void changed(ObservableValue<? extends User> ov, 
              User old_val, User new_val) {
			}
		}); 		
  	}
  	/**
  	 * Adds user to the list of users by opening a dialog for the admin to add a user.
  	 * @param event on click of Add button.
  	 * @throws Exception
  	 * @see Admin.addUser, Admin.make
  	 */
    @FXML protected void add(ActionEvent event) throws Exception {   
    	userList.getSelectionModel().clearSelection();
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Add New User");
    	dialog.setHeaderText("Enter User Name");
    	dialog.setContentText("User Name:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		if(result.get().equals("admin")){
    			Util.showErr("You can't create another admin.");
				return;  
    		}
    		if(result.get().equals("")){
    			Util.showErr("Specify valid name.");
				return;  
    		}
    		for (User u: users) {
        	    if(u.getName().equals(result.get())) {
        	    	Util.showErr("User already exists! Try again.");
					return;    
				}
    		}			
    		User tmp = new User(result.get());
			users.add(tmp);
			admin.addUser(tmp);
			admin.make(admin);
			FXCollections.sort(users);
			userList.getSelectionModel().select(tmp);					 
		} 			
    }
    /**
     * Deletes selected user from the list of users.
     * @param event on click of Delete button
     * @throws Exception
     * @see Admin.make
     */
    @FXML protected void delete(ActionEvent event) throws Exception {
    	if (users.isEmpty()){
			Util.showErr("You don't have any users.");
			return;    
		} 
    	Alert alertBox = Util.alert("Delete selected user", "Are you sure you want to delete this user?");
		if (alertBox.getResult() == ButtonType.OK) {
			 int selectedIndex = userList.getSelectionModel().getSelectedIndex();
				if (selectedIndex >= 0) {
					int j = selectedIndex;
					String name = userList.getSelectionModel().getSelectedItem().getName();
					userList.getItems().remove(selectedIndex);
					userList.getSelectionModel().select(selectedIndex++);
					admin.getUsers().remove(j);
					FXCollections.sort(users);
					Files.delete(Paths.get("dat/" + name));	
					admin.make(admin);
				}
		} else {
			alertBox.close();
		}
   
    }
    /**
     * Logs admin out and saves the list of users.
     * @param event on click of LogOut button
     * @throws Exception
     * @see Admin.make
     */
    @FXML protected void logOut(ActionEvent event) throws Exception {     
    	admin.make(admin);
    	((Node) event.getSource()).getScene().getWindow().hide();
		Parent parent = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Stage stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("Login");
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.show();
    }
    /**
     * Loads in Admin file. Creates Admin file if it doesn't exist.
     * @throws ClassNotFoundException
     * @see Admin.remake
     */
    private void load() throws ClassNotFoundException {
   	 try{
			File f = new File("dat/" + "admin");
			if(!f.exists()){
				f.createNewFile();
			} else {
				admin = Admin.remake();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
    }


		
}
