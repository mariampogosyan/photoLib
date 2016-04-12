package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Class to handle duties of an admin in PhotoAlbum as well as handling saving users.
 * 
 * @author Stephen Dacayanan, Mariam Pogosyan
 * @see User
 */
public class Admin implements Serializable {
	public static final String storeDir = "dat";
	private static final long serialVersionUID = 1L;
	private ArrayList<User> users = new ArrayList<>();
	
	private String name;

	private static Admin admin = null;
	/**
	 * Constructor for Admin class
	 * 
	 * @param name Name of admin
	 */
	public  Admin(String name) {
		this.name = name; 
	}
	
	/**
	 * Adds user to the list of users
	 * @param user User to be added
	 */
	public void addUser(User user) {
		users.add(user);
	}
	
	/**
	 * Returns name of Admin
	 * @return name of Admin
	 */
	public String getName() {
		return name;
	}
	/**
	 * Returns list of users
	 * @return List of users
	 */
	public ArrayList<User> getUsers() {
		return users;
	}
	
	/**
	 * Returns instance of Admin
	 * @param s Name of Admin
	 * @return Instance of Admin
	 */
	public  static Admin getInstance(String s) {
		if(admin == null)
			admin = new Admin(s);
		return admin;
	}
	/**
	 * Saves Admin instance to a file
	 * @param a Admin instance
	 * @throws IOException
	 */
	public void make (Admin a) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + "admin"));
		oos.writeObject(a);
		for(int i =0; i < users.size(); i++){
			ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + users.get(i).getName()));
			oo.writeObject(users.get(i));
			oo.close();
		}
		oos.close();		
	}
	/**
	 * Resaves Admin instance to an Admin file
	 * @return Admin instance
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Admin remake()throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dat/admin"));
		 admin = (Admin)ois.readObject();
		 ois.close();
		 return admin; 
	}
	
	/**
	 * Saves User instance to a file
	 * @param user Instance of User
	 * @return User instance
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see User
	 */
	public static User makeuser(User user) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream os = new ObjectInputStream(new FileInputStream("dat/"+ user.getName()));
		User u = (User) os.readObject();
		os.close();
		return u;
		
	}
}