package model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * Class that contains photos as well as their respective Caption, Tags, and Date.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 *
 */
public class Album implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 1L;
	public String name;
	private ArrayList<byte[]> bytePhotos;
	public ArrayList<String> captions;
	public Date start;
	public Date end;
	public int numPh;
	public ArrayList<Calendar> dates;
	public ArrayList<HashMap<String, String>> tags;
	/**
	 * Album constructor.
	 * @param name Name of album
	 */
	public Album (String name){ 
		this.name = name;
		start = new Date();
		end = new Date();
		bytePhotos = new ArrayList<byte[]>();
		captions = new ArrayList<String>();
		dates = new ArrayList<Calendar>();
		tags = new ArrayList<HashMap<String, String>>();
	}
	
	/**
	 * 
	 * @return Name of album
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name of Album
	 * @param name Name of album
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return Number of photos in Album
	 *
	 */
	public int getNumOfPhotos() {
		return bytePhotos.size();
	}
	/**
	 * Sets number of photos in Album
	 * @param 
	 */
	public void setNumOfPhotos(int numOfPhotos) {
		this.numPh = numOfPhotos;
	}
	/**
	 * 
	 */
	public String toString(){
		return this.name;
	}
	/**
	 * Adds photo to the Album. We save the image into the album by converting bi to a byte[] and saving that byte[] into an ArrayList. We also save the 
	 * date of the photo into another ArrayList
	 * @param bi BufferedImage containing the photo
	 * @param cal Calendar instance containing the last modified date of the photo
	 * @throws IOException
	 */
	public void addImage(BufferedImage bi, Calendar cal) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", baos);
		baos.flush();
		byte[] imageBytes = baos.toByteArray();
		baos.close();	
		bytePhotos.add(imageBytes);
		captions.add("");
		dates.add(cal);
		tags.add(new HashMap<String, String>());
	}
	/**
	 * Returns a list of the photos in an Album. Those photos are returned as BufferedImages.
	 * @return ArrayList of BufferedImages
	 * @throws IOException
	 */
	public ArrayList<BufferedImage> getBufferedImages() throws IOException {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		if (bytePhotos == null) {
			return images;
		}
		for (byte[] bp: bytePhotos) {
			InputStream in = new ByteArrayInputStream(bp);
			BufferedImage bi = ImageIO.read(in);
			images.add(bi);			
		}
		return images;
	}
	/**
	 * Removes the photo as well as its respective caption, tags, and date.
	 * @param index index of the photo in the Album
	 */
	public void removeImage(int index) {
		bytePhotos.remove(index);
		tags.remove(index);
		captions.remove(index);
		dates.remove(index);
	}
	/**
	 * Returns a BufferedImage instance of the photo
	 * @param index of the photo in the Album
	 * @return BufferedImage; null if photo is not in the Album
	 * @throws IOException
	 */
	public BufferedImage getBufferedImage(int index) throws IOException {
		BufferedImage bi = null;
		byte[] by = bytePhotos.get(index);
		if (by == null) {
			return bi;
		}
		InputStream in = new ByteArrayInputStream(by);
		bi = ImageIO.read(in);
		return bi;
	}
	/**
	 * Sets caption of a photo
	 * @param index Index of the photo
	 * @param caption String
	 */
	public void setCaption(int index, String caption) {
		captions.set(index, caption);
	}
	/**
	 * Returns the Caption of an image
	 * @param index Index of the photo in the Album
	 * @return String
	 */
	public String getCaption(int index) {
		return captions.get(index);
	}
	/**
	 * Returns Calendar instance of the photo
	 * @param index Index of the photo
	 * @return Calendar instance
	 */
	public Calendar getDate(int index) {
		return dates.get(index);
	}
	/**
	 * Returns a String consisting of a formatted Calendar instance for a photo. Represented as "month/day/year".
	 * @param index Index of the photo we need to get the date for
	 * @return String
	 */
	public String getFormattedDate(int index) {
		Calendar cal = dates.get(index);
		String[] monthNames = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		String name = monthNames[cal.get(Calendar.MONTH)];
		return (name + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.YEAR)));

	}
	/**
	 * Adds a tag for the photo specified. A tag consists of a tag key as well as a tag value.
	 * 
	 * @param tag Key
	 * @param value Value of the tag
	 * @param index Index of the photo
	 * @return true if tag added to the photo, false if tags are the same. false if tag is the same as another tag in the photo
	 */
	public boolean addTag(String tag, String value, int index) {
		Set<String> vals = tags.get(index).keySet();
		Iterator<String> it = vals.iterator();
		while (it.hasNext()) {
			String val = (String) it.next();
			if (val.equals(value)) {
				return false;
			}
		}
		tags.get(index).put(value, tag);
		return true;
	}
	/**
	 * Edits a tag in the photo specified. A tag consists of a tag key as well as a tag value.
	 * 
	 * @param tag Key
	 * @param value Value of the tag
	 * @param photoIndex Index of the photo
	 * @param prevValue Previous value of the tag
	 * @return true if tag added to the photo, false if tags are the same. false if tag is the same as another tag in the photo
	 */
	public boolean editTag(String tag, String value, String prevValue, int photoIndex) {
		Set<String> vals = tags.get(photoIndex).keySet();
		Iterator<String> it = vals.iterator();
		while (it.hasNext()) {
			String val = (String) it.next();
			if (val.equals(value)) {
				return false;
			}
		}
		tags.get(photoIndex).remove(prevValue);
		tags.get(photoIndex).put(value, tag);
		return true;
	}
	/**
	 * Deletes a tag in the photo specified. A tag consists of a tag key as well as a tag value.
	 * 
	 * @param tag Key
	 * @param value Value of the tag
	 * @param photoIndex Index of the photo
	 */
	public void removeTag(String tag, String value, int photoIndex) {
		tags.get(photoIndex).remove(value, tag);
	}
	/**
	 * Returns the tags of a photo
	 * @param index Index of the photo
	 * @return tags of a photo
	 */
	public HashMap<String, String> getTags(int index) {
		return tags.get(index);
	}
	/**
	 * Sets tags of that Photo
	 * @param index Index of a photo
	 * @param newTags Tags to be set
	 */
	public void setTags(int index, HashMap<String, String> newTags) {
		tags.set(index, newTags);
	}
	/**
	 * 
	 * @return String of the earliest date that a photo has been taken
	 */
	public String getEarliestDate() {
		int index = 0;
		Calendar res = getDate(0);
		for (int i = 1; i < dates.size(); i++) {
			Calendar tmp = getDate(i);
			int c = tmp.compareTo(res);
			if (c < 0) {
				res = tmp;
				index = i;
			}
		}
		return getFormattedDate(index);
	}
	/**
	 * 
	 * @return String of the latest date that a photo has been taken
	 */
	public String getLastDate() {
		int index = 0;
		Calendar res = getDate(0);
		for (int i = 1; i < dates.size(); i++) {
			Calendar tmp = getDate(i);
			int c = tmp.compareTo(res);
			if (c > 0) {
				res = tmp;
				index = i;
			}
		}
		return getFormattedDate(index);
	}
	/**
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		String cmp1 = this.name;
		Album a = (Album)o;
		String cmp2 = a.name;
		return cmp1.compareToIgnoreCase(cmp2);
	}
	/**
	 * Checks if there is an photo that is the same as another photo in the Album.
	 * @param bi1 BufferedImage to be added
	 * @return true if it is a new photo, false otherwise
	 * @throws IOException
	 */
	public boolean canAdd(BufferedImage bi1) throws IOException {
		
		for (int j = 0; j < getNumOfPhotos(); j++) {
			BufferedImage cmp = getBufferedImage(j);
				if(isSame(bi1, cmp)){
					return false;
				}
		}
		return true;
	}
	
	/**
	 * Compares two photos to see if they are the same by pixel count.
	 * @param img1
	 * @param img2
	 * @return true if both images are the same, false otherwise
	 */
	public boolean isSame(BufferedImage img1, BufferedImage img2) {
	    if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
	        for (int x = 0; x < img1.getWidth(); x++) {
	            for (int y = 0; y < img1.getHeight(); y++) {
	                if (img1.getRGB(x, y) == img2.getRGB(x, y)){
	                    return true;	           
	                    }
	                }
	        }
	    } else {
	        return false;
	    }
	    return true;
	}

}