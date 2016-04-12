package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;


import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Admin;
import model.Album;
import model.User;

/**
 * Controller for viewAlbum screen. User can view previews of the photos in the album as well as the photo's respective caption. User can add photos as well as
 * move a selected photo to another album. User can also delete a photo from that album. User can double click on the photo to show the photo's info as well as
 * an enlarged version of that image.
 * 
 * @author Stephen Dacayanan & Mariam Pogosyan
 * @see Album
 */
public class viewAlbumController {
	@FXML Button back;
	@FXML Button add;
	@FXML Button logout;
	@FXML Button move;
	@FXML Button remove;
	@FXML Label albumname;
	@FXML TilePane tile = new TilePane();
	@FXML ScrollPane scroll;
	//@FXML ObservableList<BufferedImage> photos;
	private List<BufferedImage> photos;
  	Admin admin = Admin.getInstance("admin");
  	User user = admin.getUsers().get(loginController.index);
	Album album = user.getAlbums().get(albumListController.index);
	int depth = 10;
	public static int index;
	/**
	 * Loads the photos as well as their respective captions into the screen.
	 * @throws IOException
	 * @see Album.getBufferedImages
	 */
	public void initialize() throws IOException { 
		try {
			photos = album.getBufferedImages();
		} catch (IOException e) {
		}
		if (photos != null) {
			loadImages(photos);			
		}
		move.setDisable(true);
		remove.setDisable(true);
 	 }
	
	/**
	 * Returns to the albumList screen.
	 * @param event on press of Back
	 * @throws Exception
	 * @see Admin.make 
	 */
	@FXML protected void back(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/albumList.fxml"));
		Util.changeScreen(event, "Album List", p, admin);
	}
	
	/**
	 * Logs user out of the program, saving the user's albums.
	 * @param event
	 * @throws Exception
	 * @see Admin.make
	 */
	@FXML protected void logOut(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Util.changeScreen(event, "LogIn", p, admin);
	}
	
	/**
	 * Adds a photo to the album. User can select a photo to add from a file explorer dialog.
	 * @param event on click of Add
	 * @throws Exception
	 * @see Album.addImage
	 */
	@FXML protected void add(ActionEvent event) throws Exception {
		FileChooser filechooser = new FileChooser();
		filechooser.setTitle("Select photo");
		filechooser.getExtensionFilters().addAll(
				new ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
				);
		File selectedFile = filechooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
		if(selectedFile != null) {
			long time = selectedFile.lastModified();
			BufferedImage addedFile = ImageIO.read(selectedFile);
			if(!album.canAdd(addedFile)){
				Util.showErr("You already have this picture!");
				return;
			}
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			cal.set(Calendar.MILLISECOND, 0);
			album.addImage(addedFile, cal);
			photos.add(addedFile);
			loadImages(photos);
			admin.make(admin);

		}	
	}
	
	/**
	 * Removes a selected photo from the album.
	 * @param event
	 * @throws Exception
	 * @see Album.removeImage
	 */
	@FXML protected void remove(ActionEvent event) throws Exception {
		if (index < 0) {
			Util.showErr("Choose photo to delete");
		}
			Alert alertBox = Util.alert("Delete selected album ", "Are you sure you want to delete this photo?");
			if (alertBox.getResult() == ButtonType.OK) {
				if (index >= 0) {
					album.removeImage(index);
					photos.remove(index);	
					loadImages(photos);
					admin.make(admin);
				} 
			} else {
				alertBox.close();
			}
	}
	
	/**
	 * Moves a photo from one album to another. User selects album to move photo to from dialog.
	 * @param event
	 * @throws Exception
	 * @see Album.removeImage, Album.addImage, Album.setCaption, Album.setTags
	 */
	@FXML protected void move(ActionEvent event) throws Exception {	
		if (index < 0) {
			Util.showErr("Choose photo to move");
		}
		List<Album> albums = user.getAlbums();
		ChoiceDialog<Album> dialog = new ChoiceDialog<Album>(albums.get(0), albums);
		dialog.setTitle("Move Album");
		dialog.setHeaderText("Move image to anther album");
		dialog.setContentText("Choose album to move image to:");
		Optional<Album> result = dialog.showAndWait();
		if (result.isPresent()) {
			BufferedImage bi = photos.get(index);
			Album toBeMovedTo = result.get();
			if(!toBeMovedTo.canAdd(bi)){
				Util.showErr("You already have this picture in that album!");
				return;
			}
			Calendar cal = album.getDate(index);
			String caption = album.getCaption(index);
			HashMap<String, String> newTags = album.getTags(index);
			album.removeImage(index);
			toBeMovedTo.addImage(bi, cal);
			toBeMovedTo.setCaption(toBeMovedTo.getNumOfPhotos() - 1, caption);
			toBeMovedTo.setTags(toBeMovedTo.getNumOfPhotos() - 1, newTags);
			photos.remove(index);	
			loadImages(photos);
		}
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
	 * Method to handle loading images to screen.
	 * @param list
	 */
	private void loadImages(List<BufferedImage> list) {
		tile.setPadding(new Insets(15, 15, 15, 15));
		tile.setHgap(15);
		tile.setVgap(15.0);
		tile.setPrefColumns(4);

		tile.getChildren().clear();
		for (BufferedImage im: list) {
	        Label caption = createImageView(im, list.lastIndexOf(im));
	        tile.getChildren().addAll(caption);
		}
	}
	/**
	 * handles creating image and it's respective caption to be loaded
	 */
	private Label createImageView(BufferedImage im, int albumIndex) {
        ImageView imageView = new ImageView();
        final Image image = SwingFXUtils.toFXImage(im, null);
        String tmp = album.getCaption(albumIndex);
        if(tmp.isEmpty()) {
        	tmp = " ";
        }
        Label caption = new Label(tmp);
		imageView.setImage(image);
		imageView.setFitWidth(150);
		imageView.setFitHeight(150);
		caption.setUserData(albumIndex);
		caption.setGraphic(imageView);
		caption.setContentDisplay(ContentDisplay.TOP);
		caption.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                    if(mouseEvent.getClickCount() == 1) {
                    	
                        DropShadow borderGlow = new DropShadow();
                        borderGlow.setOffsetY(0f);
                        borderGlow.setOffsetX(0f);
                        borderGlow.setColor(Color.BLUE);
                        borderGlow.setWidth(depth);
                        borderGlow.setHeight(depth);
                         
                        imageView.setEffect(borderGlow); //Apply the borderGlow effect to the JavaFX node
                        move.setDisable(false);
                        remove.setDisable(false);
                        index = (int) caption.getUserData();                        	
                    }
                    if(mouseEvent.getClickCount() == 2) {
                    	try {
                    		Parent p = FXMLLoader.load(getClass().getResource("/view/viewPhoto.fxml"));
                    		Util.changeScreen(mouseEvent, "Photo", p, admin);
                    	} catch (Exception e) {
                    	}
                    }
                }
            }
		});
        return caption;
    }
}
