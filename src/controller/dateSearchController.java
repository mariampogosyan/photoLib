package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import model.Admin;
import model.Album;
import model.User;

import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * Controller for dateSearch screen. User can search for photos by a date range (ie. 06/15/2015 - 10/31/2015). User can also create an album from the search results.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 *
 */
public class dateSearchController {
	Admin admin = Admin.getInstance("admin");
  	User user = admin.getUsers().get(loginController.index);
	private List<BufferedImage> photos = new ArrayList<BufferedImage>();
	ArrayList<Album> albums = user.getAlbums();
	Album tmp = new Album("tmp");
	@FXML Button back, add, logout, search;
	@FXML DatePicker from, to;
	@FXML TilePane tile;
	/**
	 * Searches for photos by a date range indicated by user.
	 * @param event on click of Search
	 * @throws Exception
	 */
	@FXML protected void search(ActionEvent event) throws Exception {		
		tile.getChildren().clear();
		if (photos != null) {
			photos.clear();
		}
		LocalDate strt = from.getValue();
		LocalDate end = to.getValue();
		if(strt == null || end == null) {
			Util.showErr("Specify the dates");
			return;
		}
		int c = strt.compareTo(end);
		if (c > 0) {
			Util.showErr("Specify valid range");
			return;			
		}
		Calendar to =  Calendar.getInstance();
		to.set(strt.getYear(), strt.getMonthValue(), strt.getDayOfMonth());
		Calendar from =  Calendar.getInstance();
		from.set(end.getYear(), end.getMonthValue(), end.getDayOfMonth());		
		int monthTo = to.get(Calendar.MONTH);
		int yearTo = to.get(Calendar.YEAR);
		int dayTo = to.get(Calendar.DAY_OF_MONTH);
		int monthFrom = from.get(Calendar.MONTH);
		int yearFrom = from.get(Calendar.YEAR);
		int dayFrom = from.get(Calendar.DAY_OF_MONTH);
		Calendar endDate = new GregorianCalendar(yearTo, monthTo, dayTo);
		Calendar startDate = new GregorianCalendar(yearFrom, monthFrom, dayFrom);
		for(Album al: albums) {
			for (int i = 0; i <al.getNumOfPhotos(); i++) {
				BufferedImage bi = al.getBufferedImage(i);
				int month = al.getDate(i).get(Calendar.MONTH);
				int year = al.getDate(i).get(Calendar.YEAR);
				int day = al.getDate(i).get(Calendar.DAY_OF_MONTH);		
				Calendar myDate = new GregorianCalendar(year, month, day);
				int cmp1 = myDate.compareTo(startDate);
				int cmp2 = myDate.compareTo(endDate);
				if (cmp2 >= 0 && cmp1 <= 0) {
					String caption = al.getCaption(i);
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
	/**
	 * Method to handle loading search results (photos)
	 * 
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
	 * Method to handle creating ImageViews.
	 * @param im
	 * @param albumIndex
	 * @return ImageView
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
	 * Logs user out of program, saving user's albums.
	 * @param event
	 * @throws Exception
	 */
	@FXML protected void logOut(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Util.changeScreen(event, "LogIn", p, admin);
	}
	/**
	 * Creates album from search results.
	 * @param event on click of Add
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
    		tile.getChildren().clear();
    		photos.clear();
    	}		
	}
	/**
	 * Returns user to albumList screen.
	 * @param event on click of Back
	 * @throws Exception
	 */
	@FXML protected void back(ActionEvent event) throws Exception {
		admin.make(admin);
		Parent p = FXMLLoader.load(getClass().getResource("/view/albumList.fxml"));
		Util.changeScreen(event, "Album", p, admin);
	}

}
