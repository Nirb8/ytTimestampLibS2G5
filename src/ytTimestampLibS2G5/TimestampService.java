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
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

import com.google.api.client.util.DateTime;

public class TimestampService {
	
	private static final Random RANDOM = new SecureRandom();
	
	private DatabaseConnectionHandler dbHandler=null;
	
	public TimestampService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	
	public boolean addTimeStamp(String Id,String caption, String videoTime, String videoId) {
		//TODO: Complete this method.
		String current = LocalDate.now().toString();
		DateTime time = new DateTime(current);
		Date uploadDate = new Date(time.getValue());
		Time time2=Time.valueOf(videoTime);
		String uniqueID = UUID.randomUUID().toString();
		Connection con=this.dbHandler.getConnection();
		try {
			CallableStatement proc =con.prepareCall("{?=call dbo.AddTimestamp(?,?,?,?,?,?)}");
			proc.setString(2, uniqueID);
			proc.setTime(3,time2);
			proc.setDate(4, uploadDate);
			proc.setString(5,videoId);
			proc.setString(6, caption);
			proc.setString(7, Id);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			System.out.println(proc.getString(1));
			proc.close();
			//need to improve error codes for addTimestamp
			
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Timestamp Entry Added");
		return true;
	}

	public ArrayList<String> getTimestamps() {
//		//TODO:Shows all the timestamps attached to a video
		ArrayList<String> timestamps = new ArrayList<String>();
		
		return timestamps;
	}
	
}
