package controller;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import model.Admin;
import model.Album;
import model.User;

/**
 * Controller for albumList screen. Users can add, delete, and view photo albums. Users can also see information about a selected album.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see User, Album
 */
public class albumListController implements Serializable{

	private static final long serialVersionUID = 1L;
	@FXML Button deleteBtn, addBtn, renameBtn, viewBtn, searchBtn,logOutBtn;
	@FXML Label  name, numOfPh, lastPh, range;
	@FXML ListView<Album> albumList;
  	@FXML private ObservableList<Album> albums;
  	Admin admin = Admin.getInstance("admin");
  	User user = admin.getUsers().get(loginController.index);
  	public static int index;
  	/**
  	 * Initializes the albumList screen. Loads in album and shows the information of the first album. Album list is in alphabetical order.
  	 * @see User.getAlbums
  	 */
	public void initialize() {
		albums = FXCollections.observableArrayList(user.getAlbums());
 		albumList.setItems(albums);
 		albumList.getSelectionModel().select(0);
 		showAlbumData(albumList.getSelectionModel().getSelectedItem());
 		albumList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>(){
			public void changed(ObservableValue<? extends Album> ov, 
              Album old_val, Album new_val) {
				showAlbumData(new_val);
			}
		}); 		
 	 }
 	/**
 	 * Adds an album to the user's albums. User can set the name of the album via a dialog.
 	 * @param event on click of Add Album
 	 * @throws Exception
 	 * @See Album
 	 */
	@FXML protected void add(ActionEvent event) throws Exception {   
		albumList.getSelectionModel().clearSelection();
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Add New Album");
    	dialog.setHeaderText("Enter Album Name");
    	dialog.setContentText("Album Name:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		for (Album a: albums) {
        	    if(a.getName().equals(result.get())) {
        	    	Util.showErr("Album already exists! Give another name.");
					return;    
				}
    		}			
    		Album tmp = new Album(result.get());
			albums.add(tmp);
			user.getAlbums().add(tmp);
			FXCollections.sort(albums);
			albumList.getSelectionModel().select(tmp);	
			showAlbumData(tmp);
			admin.make(admin);

		} 		
    }
	/**
	 * Deletes selected album from user's albums.
	 * @param event on click of Delete Album
	 * @throws Exception
	 * 
	 */
	@FXML protected void delete(ActionEvent event) throws Exception {   
		if (albums.isEmpty()){
			Util.showErr("You don't have any albums.");
			return;    
		} 
		Alert alertBox = Util.alert("Delete selected album ", "Are you sure you want to delete this album?");
		if (alertBox.getResult() == ButtonType.OK) {
			 int selectedIndex = albumList.getSelectionModel().getSelectedIndex();
				if (selectedIndex >= 0) {
					int j = selectedIndex;
					albumList.getItems().remove(selectedIndex);
					albumList.getSelectionModel().select(selectedIndex++);
					user.getAlbums().remove(j);
					FXCollections.sort(albums);
					admin.make(admin);
				}
		} else {
			alertBox.close();
		}
	
    }
	/**
	 * Renames the selected album in the user's albums. Opens dialog for user to rename the album.
	 * @param event on click of Rename Album
	 * @throws Exception
	 */
	@FXML protected void rename(ActionEvent event) throws Exception {
		if (albums.isEmpty()){
			Util.showErr("You don't have any albums.");
			return;    
		} 
	 	 int selectedIndex = albumList.getSelectionModel().getSelectedIndex();
	 	 if (selectedIndex >= 0) {
	 		TextInputDialog dialog = new TextInputDialog();
	    	dialog.setTitle("Edit Name");
	    	dialog.setHeaderText("Enter New Name");
	    	dialog.setContentText("Album Name:");
	    	Optional<String> result = dialog.showAndWait();
	    	if (result.isPresent()){
	    		for (Album a: albums) {
	        	    if(a.getName().equals(result.get()) && a != albumList.getSelectionModel().getSelectedItem() ) {
	        	      	Util.showErr("Album already exists! Give another name.");
						return;    
					} 
	    		}
	    		albumList.getSelectionModel().getSelectedItem().setName(result.get());
				FXCollections.sort(albums);
	    	}
		}
    }
	/**
	 * Opens a viewAlbum screen where user can view photos in the selected album.
	 * @param event on click of View Album
	 * @throws Exception
	 * @see viewAlbumController
	 */
	@FXML protected void view(ActionEvent event) throws Exception {		
		//Album selected = albumList.getSelectionModel().getSelectedItem();
		if (albums.isEmpty()) {
			Util.showErr("You don't have any albums.");
			return;
		} else {
			index = albumList.getSelectionModel().getSelectedIndex();
			Parent p = FXMLLoader.load(getClass().getResource("/view/viewAlbum.fxml"));
			Util.changeScreen(event, "Album", p, admin);
		}
		//String name = selected.getName();
	}
	/**
	 * Opens dialog for user to select whether the user wants to search by Tag-Value pair or by date-range. After selection, opens dateSearch or tagSearch page.
	 * @param event on click of Search
	 * @throws Exception
	 * @see tagSearchController, dateSearchController
	 */
	@FXML protected void search(ActionEvent event) throws Exception {
		List<String> choices = new ArrayList<>();
		choices.add("by Tags");
		choices.add("by Date Range");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("by Tags", choices);
		dialog.setTitle("Search");
		dialog.setHeaderText("Search for specific photos");
		dialog.setContentText("Choose parameter for search:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			if (result.get() == "by Tags") {
				admin.make(admin);
				Parent p = FXMLLoader.load(getClass().getResource("/view/tagSearch.fxml"));
				Util.changeScreen(event, "Search by Tag", p, admin);
				
			} else if(result.get() == "by Date Range") {
				admin.make(admin);
				Parent p = FXMLLoader.load(getClass().getResource("/view/dateSearch.fxml"));
				Util.changeScreen(event, "Search by Range", p, admin);
							
			}
		}		
	}
	/**
	 * Logs user out of program, saving the albums the user has made.
	 * @param event on click of Logout
	 * @throws Exception
	 * @see Admin.make
	 */
	@FXML protected void logOut(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Util.changeScreen(event, "LogIn", p, admin);
	}
	/**
	 * Method that handles updates of album data.
	 * @param album
	 * @see Album
	 */
	private void showAlbumData(Album album) {
		if (album != null) {
			name.setText(album.getName());
			String num = String.valueOf(album.getNumOfPhotos());
			numOfPh.setText(num);
			if (album.getNumOfPhotos() > 0) {
				lastPh.setText(album.getLastDate());
				range.setText(album.getEarliestDate() + " - " + album.getLastDate());
			} else {
				lastPh.setText("");
				range.setText("");
			}
		}
	}
	
}

	
