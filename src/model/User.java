package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that consists of a user's albums.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see Album, PhotoAlbum
 */
public class User implements Serializable, Comparable<Object> {
	private static final long serialVersionUID = 1L;
	ArrayList<Album> albums;
	public String name;	
	/**
	 * User constructor.
	 * @param name Name of user.
	 */
	public User(String name){ 
		this.name = name;
		albums =  new ArrayList<Album>();
	}
	/**
	 * 
	 * @return List of Album objects for User
	 * @see Album
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	/**
	 * 
	 * @return Name of user
	 */
	public String getName() {
		return name;
	}	
	/**
	 * Sets name of User
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 */
	public String toString(){
		return this.name;
	}
	/**
	 * 
	 * @return All photos that a User has saved in his/her Albums.
	 * @throws IOException
	 * @see Album.getBufferedImages
	 */
	public ArrayList<BufferedImage> getAllPhotos() throws IOException {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		for (Album al : albums) {
			images.addAll(al.getBufferedImages());		
		}
		return images;
	}
	/**
	 * 
	 */
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			 return false;
		}		
		User other = (User) o;		
		return name == other.name;
	}
	/**
	 * 
	 */
	@Override
	public int compareTo(Object o) {
		String cmp1 = this.name;
		User us = (User)o;
		String cmp2 = us.name;
		return cmp1.compareToIgnoreCase(cmp2);
	}

}
