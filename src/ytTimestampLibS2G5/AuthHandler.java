package ytTimestampLibS2G5;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

import SpecialClasses.TableList;
import javafx.animation.KeyValue.Type;

public class AuthHandler {
	
	private static final Random RANDOM = new SecureRandom();
	private static final Base64.Encoder enc = Base64.getEncoder();
	private static final Base64.Decoder dec = Base64.getDecoder();
	
	private final DatabaseConnectionHandler dbHandler;
	private String currentUserId;
	private String currentUserName;
	private String favoriteContent;
	
	public AuthHandler(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
		this.dbHandler.connect();
	}
	
	public boolean login(String username, String password) {
		//DONE: Complete this method.
		Connection con=this.dbHandler.getConnection();
		String query ="SELECT PasswordSalt, PasswordHash, UserID, FavoriteContentType  from \"Users\" WHERE Username=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1, username);
			ResultSet rs = prpstmt.executeQuery();
			rs.next();
			this.currentUserId=rs.getString("UserID");
			this.currentUserName=username;
			byte[] salt =rs.getBytes("PasswordSalt");
			String checkHash = rs.getString("PasswordHash");
			this.favoriteContent = rs.getString("FavoriteContentType");
			String hashInput = this.hashPassword(salt, password);
			if (hashInput.equals(checkHash)) {
				System.out.println("Login Successful. Welcome back, " + username + "!");
				return true;
			}
			else {
				System.out.println("Login Failed: Incorrect username or password");
			}
		}
		catch(SQLException e) {
			System.out.println("Login Failed: User does not exist");
		}
		return false;
	}

	public boolean register(String username, String password) {
		//DONE:
		Connection con = this.dbHandler.getConnection();
		byte[] rand =this.getNewSalt();
		String hash =this.hashPassword(rand,password);
		String uniqueID = UUID.randomUUID().toString();
		try {
		CallableStatement proc =con.prepareCall("{?=call dbo.RegisterUser(?,?,?,?)}");
		proc.setString(2,uniqueID);
		proc.setString(3, username);
		proc.setString(4, hash);
		proc.setBytes(5, rand);
		proc.registerOutParameter(1, Types.INTEGER);
		proc.execute();
		int returnValue = proc.getInt(1);
		this.currentUserId=uniqueID;
		this.currentUserName=username;
		this.favoriteContent=null;
		proc.close();
		
		if (returnValue==2) {
			throw new Error("ERROR: Username cannot be null or empty.");
		}
		if (returnValue==6) {
			throw new Error("ERROR: UserID already in use.");
		}
		if (returnValue==7) {
			throw new Error("ERROR: Username already exists.");
		}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Registeration completed");
		return true;
	}
	
	public byte[] getNewSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return salt;
	}
	
	public String getStringFromBytes(byte[] data) {
		return enc.encodeToString(data);
	}

	public String hashPassword(byte[] salt, String password) {

		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory f;
		byte[] hash = null;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
			e.printStackTrace();
		}
		return getStringFromBytes(hash);
	}
	
	public String getCurrentUser() {
		return this.currentUserId;
	}
	public String getCurrentUserName() {
		return this.currentUserName;
	}
	public String getFavoriteContent() {
		return this.favoriteContent;
	}
	//Shows infor on the user
	public void showUserProfile() {
		Connection con = this.dbHandler.getConnection();
		String query="SELECT * FROM dbo.GetProfile() WHERE UserName=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1, this.currentUserName);
			ResultSet rs =prpstmt.executeQuery();
			TableList table = new TableList(4, "User ID","Username","Date of Birth","Favorite Content Type");
			while (rs.next()){
			String ID =rs.getString(1);
			String Username=rs.getString(2);
			String DOB=null;
			if (rs.getDate(3)!=null) {
				DOB=rs.getDate(3).toString();
			}
			String FContent=null;
			if (rs.getString(4)!=null) {
				FContent = rs.getString(4);
			}
			table.addRow(ID,Username,DOB,FContent);
			}
			table.print();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	//allows for the DOB to be updated by user
	public boolean updateDOB(String DOB) {
		Date date =Date.valueOf(DOB);
		Connection con = this.dbHandler.getConnection();
		try {
			CallableStatement proc =con.prepareCall("{?=call dbo.ChangeDOB(?,?)}");
			proc.setString(2,this.currentUserId);
			proc.setDate(3, date);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			if (returnValue ==2) {
				throw new Error("Error:Invalid DOB");
			}
			if (returnValue==0) {
				System.out.println("Updated DOB");
			}
			proc.close();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	//allows for the favoriteContentType to be updated by user
	public boolean updateFavoriteContentType(String ID) {
		int id = Integer.valueOf(ID);
		
		Connection con = this.dbHandler.getConnection();
		try {
			CallableStatement proc = con.prepareCall("{?=call dbo.UpdateFavContentType(?,?)}");
			proc.setString(2, this.currentUserId);
			proc.setInt(3, id);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			if (returnValue ==2) {
				throw new Error("Error:Invalid Content ID");
			}
			if (returnValue==0) {
				System.out.println("Updated Favorite Content Type");
				this.favoriteContent=ID;
			}
			proc.close();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	//allows for the Username to be updated by the user
	public boolean updateUsername(String newUsername) {
		Connection con=this.dbHandler.getConnection();
		try {
			CallableStatement proc = con.prepareCall("{?=call dbo.UpdateUsername(?,?)}");
			proc.setString(2, this.currentUserId);
			proc.setString(3, newUsername);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			if (returnValue ==1) {
				throw new Error("Error: UserID cannot be null");
			}
			if (returnValue ==2) {
				throw new Error("Error: Username cannot be null");
			}
			if (returnValue ==3) {
				throw new Error("Error: Username already exists");
			}
			if (returnValue==4) {
				throw new Error("Error: User does not exist");
			}
			if (returnValue==0) {
				System.out.println("Updated Username");
				this.currentUserName=newUsername;
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//allows for a user's password to be updated
	public boolean updatePassword(String newPassword) {
		Connection con = this.dbHandler.getConnection();
		byte[] rand =this.getNewSalt();
		String hash =this.hashPassword(rand,newPassword);
		try {
			CallableStatement proc = con.prepareCall("{?=call dbo.ChangePassword(?,?,?)}");
			proc.setString(2, this.currentUserId);
			proc.setBytes(3, rand);
			proc.setString(4, hash);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			if (returnValue==1) {
				throw new Error("UserID cannot be null");
			}
			if (returnValue==2) {
				throw new Error("User does not exist");
			}
			if (returnValue==3) {
				throw new Error("Password cannot be null");
			}
			if (returnValue==4) {
				throw new Error("Password Salt cannot be null");
			}
			if (returnValue ==0) {
				System.out.println("Password updated");
			}
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//checks to seee if input is a valid password for a user
	public boolean validatePassword(String input) {
		Connection con=this.dbHandler.getConnection();
		String query ="SELECT PasswordSalt, PasswordHash, UserID from \"Users\" WHERE Username=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1, this.currentUserName);
			ResultSet rs = prpstmt.executeQuery();
			rs.next();
			byte[] salt =rs.getBytes("PasswordSalt");
			String checkHash = rs.getString("PasswordHash");
			String hashInput = this.hashPassword(salt, input);
			if (hashInput.equals(checkHash)) {
				return true;
			}
			else {
				System.out.println("Incorrect Password");
				return false;
			}
		}
		catch(SQLException e) {
			System.out.println("Login Failed: User does not exist");
		}
		return false;
	}
}
