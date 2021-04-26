package ytTimestampLibS2G5;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

public class TimestampService {
	
	private static final Random RANDOM = new SecureRandom();
	
	private DatabaseConnectionHandler dbHandler=null;
	
	public TimestampService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	
	public boolean addTimeStamp(String username, String password) {
		//TODO: Complete this method.
		return false;
	}

	public ArrayList<String> getTimestamps() {
//		//TODO:
		ArrayList<String> timestamps = new ArrayList<String>();
		
		return timestamps;
	}
	
}
