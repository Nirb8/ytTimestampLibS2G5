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

import SpecialClasses.TableList;

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
			//System.out.println(proc.getString(1));
			proc.close();
			//need to improve error codes for addTimestamp
			if (returnValue ==2) {
				VideoService videoService= new VideoService(this.dbHandler);
				boolean x =videoService.addVideo(videoId);
				if (x) {
					this.addTimeStamp(Id, caption, videoTime, videoId);
				}
			}
			if (returnValue==1) {
				throw new Error("ERROR: TimestampID cannot be null");
			}
			if (returnValue==3) {
				throw new Error("ERROR: Time cannot be null");
			}
			if (returnValue==5) {
				throw new Error("ERROR: TimestampID already in use");
			}
			System.out.println("Timestamp Entry Added");
			
			if (returnValue==0) {
				boolean result = createTimestampTag(uniqueID,videoId);
				if (result) {
					System.out.println("TimestampTag created");
					return true;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}

	public ArrayList<ArrayList<String>> getTimestamps(String videoID) {
		//DONE:Shows all the timestamps attached to a video
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> timestamps = new ArrayList<ArrayList<String>>();
		String query;
		
		//if no input is given
		if (videoID.isEmpty()) {
			System.out.println("Basic Search");
			query ="SELECT * FROM dbo.UserView";
			try {
				Statement stmt = con.createStatement();
				ResultSet rs=stmt.executeQuery(query);
				while (rs.next()) {
					String ID = rs.getString(1);
					String name = rs.getString(2);
					String des = rs.getString(3);
					String tTime = rs.getTime(4).toString();
					String cType = rs.getString(5);
					String cTime = rs.getDate(6).toString();
					ArrayList<String> details = new ArrayList<String>();
					details.add(ID);
					details.add(name);
					details.add(des);
					details.add(tTime);
					details.add(cType);
					details.add(cTime);
					timestamps.add(details);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		//if input is given
		else {
			System.out.println("Specified Search");
			query="SELECT * FROM dbo.UserView WHERE [YouTube ID]=?";
			try {
				PreparedStatement prpstmt = con.prepareStatement(query);
				prpstmt.setString(1, videoID);
				ResultSet rs=prpstmt.executeQuery();
				while (rs.next()) {
					String ID = rs.getString(1);
					String name = rs.getString(2);
					String des = rs.getString(3);
					String tTime = rs.getTime(4).toString();
					String cType = rs.getString(5);
					String cTime = rs.getDate(6).toString();
					ArrayList<String> details = new ArrayList<String>();
					details.add(ID);
					details.add(name);
					details.add(des);
					details.add(tTime);
					details.add(cType);
					details.add(cTime);
					timestamps.add(details);
				}
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return timestamps;
	}
	
	//Used specifically for timestamp search output table
	public void outputConsoleTables(ArrayList<ArrayList<String>> results) {
		TableList table = new TableList(6,"YouTube ID", "Video Name", "Description", "TimestampTime", "Content Type", "Created Time");
		results.forEach(element -> table.addRow(element.get(0),element.get(1), element.get(2), element.get(3), element.get(4),element.get(5)));
		table.print();
	}
	
	public boolean createTimestampTag(String TimestampID, String videoID) {
		Connection con=this.dbHandler.getConnection();
		String query="SELECT ContentTypeID FROM dbo.VideoGenres WHERE YTVideoID=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1,videoID);
			ResultSet rs = prpstmt.executeQuery();
			rs.next();
			String contentID = rs.getString("ContentTypeID");
			
			CallableStatement proc = con.prepareCall("{?=call dbo.InsertIntoTimestampTags(?,?)}");
			proc.setString(3,TimestampID);
			proc.setString(2, contentID);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			proc.close();
			
			if(returnValue==1) {
				throw new Error("ERROR: ContentTypeID cannot be null");
			}
			if(returnValue==2) {
				throw new Error("ERROR: ContentType value does not exist");
			}
			if(returnValue==3) {
				throw new Error("ERROR: TimestampID cannot be null");
			}
		}	
		catch(Exception e) {
		e.printStackTrace();
		System.out.println("Error");
		return false;
		}
		return true;
	}
	
	public boolean deleteTimestamp(String Characteristic, String input, String userID) {
		//TODO
		Connection con = this.dbHandler.getConnection();
		String query = "SELECT AuthorID FROM TimeStamps WHERE UserID="+userID+"AND ?=?";
		
		//PreparedStatement prpsmt = con.prepareStatement(sql)
		return false;
		
	}
	
}
