package ytTimestampLibS2G5;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

public class AuthHandler {
	
	private static final Random RANDOM = new SecureRandom();
	private static final Base64.Encoder enc = Base64.getEncoder();
	private static final Base64.Decoder dec = Base64.getDecoder();
	
	
	private DatabaseConnectionHandler dbHandler;
	
	public AuthHandler(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	
	public boolean login(String username, String password) {
		//TODO: Complete this method.
		
//		PreparedStatement pstmt = null;
//		String paramString = "SELECT PasswordSalt, PasswordHash From [User] Where Username = ?;";
//		
//		try {
//			pstmt = this.dbService.getConnection().prepareStatement(paramString);
//			
//			if(username == null || username.isEmpty()) {
//				JOptionPane.showMessageDialog(null, "Login Failed");
//				return false;
//			} else {
//				pstmt.setString(1, username);
//			}
//			
//			ResultSet rs = pstmt.executeQuery();
//			
//			int saltIndex = rs.findColumn("PasswordSalt");
//			int hashIndex = rs.findColumn("PasswordHash");
//			
//			if(rs.next()) {
//				//continue normally
//			} else {
//				//fails because no user exists with that username
//				JOptionPane.showMessageDialog(null, "Login Failed");
//				return false;
//			}
//			
//			byte[] salt = rs.getBytes(saltIndex);
//			String hashedPassword = rs.getString(hashIndex);
//			
//			
//			
//			String enteredHashedPassword = hashPassword(salt, password);
//			
//			//System.out.println("current hash: " + hashedPassword);
//			//System.out.println("entered hash: " + enteredHashedPassword);
//			
//			if(hashedPassword.equals(enteredHashedPassword)) {
//				return true;
//			} else {
//				JOptionPane.showMessageDialog(null, "Login Failed");
//				return false;
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		return false;
	}

	public boolean register(String username, String password) {
//		//TODO: Task 6
//		//obtain new password salt
//		byte[] salt = getNewSalt();
//		//use salt and password to obtain hashed password
//		String hashedPassword = hashPassword(salt, password);
//		
//		System.out.println(hashedPassword);
//		CallableStatement cstmt = null;
//		try {
//			cstmt = this.dbService.getConnection().prepareCall("{? = call Register(?, ?, ?)}");
//			
//			cstmt.registerOutParameter(1, Types.INTEGER);
//			
//			if(username == null || username.isEmpty()) {
//				JOptionPane.showMessageDialog(null, "Username is a required field."); //due to schema restName must be not null since it's the key
//				return false; //don't continue with the procedure call, front end side error checking
//			} else {
//				cstmt.setString(2, username);
//			}
//			
//			//cstmt.setString(3, getStringFromBytes(salt));
//			cstmt.setBytes(3, salt);
//			
//			cstmt.setString(4, hashedPassword);
//			
//			cstmt.execute();
//			
//			int returnValue = cstmt.getInt(1);
//			
//			switch(returnValue) {
//			case 0:
//				//no error, do nothing
//				return true;
//			case 1:
//				//JOptionPane.showMessageDialog(null, "Restaurant Name is a required field.");
//				//break;
//			case 2:
//				//JOptionPane.showMessageDialog(null, "PasswordSalt cannot be null or empty.");
//				//break;
//			case 3:
//				//JOptionPane.showMessageDialog(null, "PasswordHash cannot be null or empty.");
//				//break;
//			case 4:
//				//JOptionPane.showMessageDialog(null, "Username \"" + username + "\" is already in use.");
//				JOptionPane.showMessageDialog(null, "Registration Failed");
//				return false;
//			}
//			
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		return false;
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
		} catch (NoSuchAlgorithmException e) {
			JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
			e.printStackTrace();
		}
		return getStringFromBytes(hash);
	}

}
