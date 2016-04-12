package controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;
import model.Admin;
import model.Album;
import model.User;
/**
 * Controller for viewPhoto screen. User can view a larger view of the image as well as the image's respective caption, tags, and date of the image.
 * User can also move to another image (manual slideshow).
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see Album
 */
public class viewPhotoController {
	@FXML Button editCaption, prev, next, logOut, back, search, editTag, addTag, deleteTag;
	@FXML Label caption, date;
	@FXML ImageView photo;
	@FXML TableView<Map.Entry<String, String>> tagList;
	@FXML ObservableList<Map.Entry<String, String>> tags;
  	@FXML private ObservableList<Album> albums;
	Admin admin = Admin.getInstance("admin");
  	User user = admin.getUsers().get(loginController.index);
	Album album = user.getAlbums().get(albumListController.index);
	public int photoIndex = viewAlbumController.index;
	/**
	 * Initializes the viewPhoto screen
	 * @throws IOException
	 */
	public void initialize() throws IOException {
		BufferedImage bi = album.getBufferedImage(viewAlbumController.index);
		final Image image = SwingFXUtils.toFXImage(bi, null);
		photo.setImage(image);
		showImageData(photoIndex);
	}
	
	/**
	 * Opens dialog for user to select whether the user wants to search by Tag-Value pair or by date-range. After selection, opens dateSearch or tagSearch page.
	 * @param event on click of Search
	 * @throws Exception
	 * @see dateSearchController, tagSearchController
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
	 * Opens a viewPhoto screen with the previous photo in the album.
	 * @param event On click of prev
	 * @throws Exception 
	 */
	@FXML protected void prevPhoto(ActionEvent event) throws Exception {
		if (photoIndex - 1 < 0) {
			viewAlbumController.index = album.getNumOfPhotos() - 1;
		} else {
			viewAlbumController.index--;
		}

		Parent p = FXMLLoader.load(getClass().getResource("/view/viewPhoto.fxml"));
		Util.changeScreen(event, "Photo", p, admin);
	}
	
	/**
	 * Opens a viewPhoto screen with the next photo in the album.
	 * @param event on click of next
	 * @throws Exception
	 */
	@FXML protected void nextPhoto(ActionEvent event) throws Exception {
		if (photoIndex + 1 > album.getNumOfPhotos() - 1) {
			viewAlbumController.index = 0;
		} else {
			viewAlbumController.index++;
		}
		//System.out.println(viewAlbumController.index);
		Parent p = FXMLLoader.load(getClass().getResource("/view/viewPhoto.fxml"));
		Util.changeScreen(event, "Photo", p, admin);
	}
	
	/**
	 * Edits the caption of the photo via a dialog.
	 * @param event on click of Edit Caption
	 * @throws Exception
	 * @see Album.setCaption
	 */
	@FXML protected void editCaption(ActionEvent event) throws Exception {
		TextInputDialog dialog = new TextInputDialog(album.getCaption(photoIndex));
		dialog.setTitle("Edit Caption");
		dialog.setHeaderText("Enter Caption");
		dialog.setContentText("Caption:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String cap = result.get();
		    album.setCaption(photoIndex, cap);
		}
		showImageData(photoIndex);
	}
	
	/**
	 * Adds tag to the photo via a dialog. A Tag consists of a tag type and value of the tag.
	 * @param event on click of Add Tag
	 * @throws Exception
	 * @see Album.addTag
	 */
	@FXML protected void addTag(ActionEvent event) throws Exception {
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Add Tag");
		dialog.setHeaderText("Add Tag");

		// Set the button types.
		ButtonType addTagType = new ButtonType("Add Tag", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addTagType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField key = new TextField();
		key.setPromptText("Key");
		TextField value = new TextField();
		value.setPromptText("Value");

		grid.add(new Label("Tag Type:"), 0, 0);
		grid.add(key, 1, 0);
		grid.add(new Label("Tag Value"), 0, 1);
		grid.add(value, 1, 1);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == addTagType) {
		        return new Pair<>(key.getText(), value.getText());
		    }
		    return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		if (result.isPresent()){
			Pair<String, String> tag = result.get();
			if (tag.getKey().equals("") || tag.getValue().equals("")) {
				Util.showErr("Invalid tag values. Please try again.");
				return;
			} else {
				if (album.addTag(tag.getKey(), tag.getValue(), photoIndex) == false) {
			    	Util.showErr("Tag already exists! Give another tag.");
					return;
			    }
			    album.addTag(tag.getKey(), tag.getValue(), photoIndex);
				showImageData(photoIndex);
			}		    
		}
	}
	
	/**
	 * Edits the selected tag in the photo.
	 * @param event on click of Edit Selected Tag
	 * @throws Exception
	 * @see Album.editTag
	 */
	@FXML protected void editTag(ActionEvent event) throws Exception {
		int i = tagList.getSelectionModel().getSelectedIndex();
		if (i < 0) {
			Util.showErr("Chose tag that you want to edit");
			return;
		}
		Map.Entry<String, String> tag = tagList.getSelectionModel().getSelectedItem();
		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Edit Tag");
		dialog.setHeaderText("Edit Tag");

		// Set the button types.
		ButtonType addTagType = new ButtonType("Edit Tag", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addTagType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField key = new TextField(tag.getValue());
		key.setPromptText("Key");
		TextField value = new TextField(tag.getKey());
		value.setPromptText("Value");

		grid.add(new Label("Tag Type:"), 0, 0);
		grid.add(key, 1, 0);
		grid.add(new Label("Tag Value"), 0, 1);
		grid.add(value, 1, 1);
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == addTagType) {
		        return new Pair<>(key.getText(), value.getText());
		    }
		    return null;
		});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();
		if (result.isPresent()){
			Pair<String, String> tmp = result.get();
			if (tmp.getKey().equals("") || tmp.getValue().equals("")) {
				Util.showErr("Invalid tag values. Please try again.");
				return;
			} else {
			    if (album.editTag(tmp.getKey(), tmp.getValue(), tag.getKey(), photoIndex) == false) {
			    	Util.showErr("Tag already exists! Give another tag.");
					return;
			    }
			    album.editTag(tmp.getKey(), tmp.getValue(), tag.getKey(), photoIndex);
				showImageData(photoIndex);
			}
		}
	}
	
	/**
	 * Deletes a selected Tag from the photo.
	 * @param event on click of Delete Selected Tag
	 * @throws Exception
	 * @see Album.removeTag
	 */
	@FXML protected void deleteTag(ActionEvent event) throws Exception {
		int i = tagList.getSelectionModel().getSelectedIndex();
		if (i < 0) {
			Util.showErr("Chose tag that you want to delete");
			return;
		}
		Map.Entry<String, String> tag = tagList.getSelectionModel().getSelectedItem();
		album.removeTag(tag.getValue(), tag.getKey(), photoIndex);
		showImageData(photoIndex);
	}
	
	/**
	 * Logs user out of the program, saving that user's albums
	 * @param event on click of LogOut
	 * @throws Exception
	 * @see Admin.make
	 */
	@FXML protected void logOut(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Util.changeScreen(event, "LogIn", p, admin);
	}
	
	/**
	 * Returns user to the viewAlbum screen.
	 * @param event on click of Back
	 * @throws Exception
	 */
	@FXML protected void back(ActionEvent event) throws Exception {
		Parent p = FXMLLoader.load(getClass().getResource("/view/viewAlbum.fxml"));
		Util.changeScreen(event, "Album", p, admin);
	}
	
	/**
	 * Method to handle changes in the photo data.
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void showImageData(int index) {
		if (album.getCaption(index) != null) {
			caption.setText(album.getCaption(index));
		}
		if (album.getDate(index) != null) {
			date.setText(album.getFormattedDate(index));
		}
		HashMap<String, String> hmap = album.getTags(photoIndex);
		TableColumn<Map.Entry<String, String>, String> column1 = new TableColumn<>("Key");
        column1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // this callback returns property for just one cell, you can't use a loop here
                // for first column we use key
                return new SimpleStringProperty(p.getValue().getValue());
            }
        });
        TableColumn<Map.Entry<String, String>, String> column2 = new TableColumn<>("Value");
        column2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                // for second column we use value
                return new SimpleStringProperty(p.getValue().getKey());
            }
        });
        tags = FXCollections.observableArrayList(hmap.entrySet());
        tagList.setItems(tags);
        tagList.getColumns().setAll(column1, column2);
	}

}