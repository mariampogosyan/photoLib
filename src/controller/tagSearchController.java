package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import model.Admin;
import model.Album;
import model.User;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Controller for tagSearch screen. User can search for photos by a tag-value pair (ie. Artist, Kanye West). Users can also create an album from the search results.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 *
 */
public class tagSearchController {
	Admin admin = Admin.getInstance("admin");
  	User user = admin.getUsers().get(loginController.index);
	private List<BufferedImage> photos = new ArrayList<BufferedImage>();
	ArrayList<Album> albums = user.getAlbums();
	Album tmp = new Album("tmp");
	@FXML Button back, add, logout, search;
	@FXML TextField type, tag;
	@FXML TilePane tile;
	/**
	 * Searches for photos that contain a Tag-Value pair indicated by the user
	 * @param event
	 * @throws Exception
	 */
	@FXML protected void search(ActionEvent event) throws Exception {		
		tile.getChildren().clear();
		if (photos != null) {
			photos.clear();
		}
		if(type.getText().isEmpty() || tag.getText().isEmpty()) {
			Util.showErr("Specify all fields");
			return;
		}
		String typeTag = type.getText();
		String tags = tag.getText();
		for(Album al: albums) {
			for (int i = 0; i <al.getNumOfPhotos(); i++) {
				BufferedImage bi = al.getBufferedImage(i);
				String caption = al.getCaption(i);
				HashMap<String, String> cmp = al.getTags(i);
				for (HashMap.Entry<String, String> entry : cmp.entrySet()) {
				    String key = entry.getKey();
				    Object value = entry.getValue();
				    if(key.equals(tags) && value.equals(typeTag)) {
						if(tmp.canAdd(bi)){
							tmp.addImage(bi, al.getDate(i));
							tmp.setCaption(tmp.getNumOfPhotos() - 1, caption);
							HashMap<String, String> newTags = al.getTags(i);
							tmp.setTags(tmp.getNumOfPhotos() - 1, newTags);
				    }
						photos.add(bi);

				}

			}
		}

		if(!photos.isEmpty()) {
			loadImages(photos);
		} else {
			Util.showErr("No results");			
		}	
		}
	}
	/**
	 * Method for handling loading search results
	 * @param list
	 */
	private void loadImages(List<BufferedImage> list) {		
		tile.setPadding(new Insets(15, 15, 15, 15));
		tile.setHgap(15);
		tile.setVgap(15.0);
		tile.setPrefColumns(4);
		tile.getChildren().clear();
		for (BufferedImage im: list) {
			ImageView imageView;
			imageView = createImageView(im, list.lastIndexOf(im));
			tile.getChildren().addAll(imageView);
		}
	}
	/**
	 * Method for creating ImageView to be loaded
	 * @param im
	 * @param albumIndex
	 * @return
	 */
	private ImageView createImageView(BufferedImage im, int albumIndex) {
        ImageView imageView = new ImageView();
        final Image image = SwingFXUtils.toFXImage(im, null);
		imageView.setImage(image);
		imageView.setFitWidth(150);
		imageView.setFitHeight(150);
		imageView.setUserData(albumIndex);
        return imageView;
    }
	/**
	 * Logs user out of program, saving the user's albums.
	 * @param event
	 * @throws Exception
	 */
	@FXML protected void logOut(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Util.changeScreen(event, "LogIn", p, admin);
	}
	/**
	 * Adds a new album to the user's list of albums containing the search results.
	 * @param event
	 * @throws Exception
	 */
	@FXML protected void add(ActionEvent event) throws Exception {
		if (photos.isEmpty()) {
			Util.showErr("There is no photos to add");
			return;
		}
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
			user.getAlbums().add(tmp);
    		tmp.setName(result.get());
    		
    	}		
	}
	/**
	 * Returns to the albumList screen.
	 * @param event
	 * @throws Exception
	 */
	@FXML protected void back(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/albumList.fxml"));
		Util.changeScreen(event, "Album", p, admin);
	}

}
