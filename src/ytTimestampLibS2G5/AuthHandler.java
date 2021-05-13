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
		String query ="SELECT PasswordSalt, PasswordHash, UserID  from \"Users\" WHERE Username=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1, username);
			ResultSet rs = prpstmt.executeQuery();
			rs.next();
			this.currentUserId=rs.getString("UserID");
			this.currentUserName=username;
			byte[] salt =rs.getBytes("PasswordSalt");
			String checkHash = rs.getString("PasswordHash");
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
//			e.printStackTrace();
			System.out.println("Login Failed: User does not exist");
		}
		return false;
	}

	public boolean register(String username, String password) {
//		//DONE: Task 6 WORKS!!!!
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
		//System.out.println(proc.getString(1));
		this.currentUserId=uniqueID;
		this.currentUserName=username;
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
	
}
