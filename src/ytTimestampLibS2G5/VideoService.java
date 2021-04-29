package ytTimestampLibS2G5;

import java.io.InputStreamReader;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JOptionPane;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;

public class VideoService {
	
	private static final Random RANDOM = new SecureRandom();
	//private boolean connectionStatus=false;
	private DatabaseConnectionHandler dbHandler=null;
	
	public VideoService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	
	public boolean addVideo(String YouTubeID,String title,Date uploadDate, String time, int contentType, String contentName ) {
		//TODO: Complete this method.
		//for some reason the time.value of gives out invalid parameter if I try to take it from the console
		//DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		//LocalDate  d1 = LocalDate.parse(uploadDate, df);
		Date date = uploadDate;
		System.out.println(time);
		Time duration = Time.valueOf(time);
		//String videoID = UUID.randomUUID().toString();
		Connection con=this.dbHandler.getConnection();
		try {
			CallableStatement proc =con.prepareCall("{?=call dbo.AddVideo(?,?,?,?)}");
			proc.setString(2,YouTubeID);
			proc.setString(3, title);
			proc.setTime(4, duration);
			proc.setDate(5, date);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			System.out.println(proc.getString(1));
			proc.close();
			if (returnValue==1) {
				throw new Error("ERROR: VideoID cannot be null");
			}
			if (returnValue==2) {
				throw new Error("ERROR: Video Title cannot be null");
			}
			if (returnValue==3) {
				throw new Error("ERROR: Duration time cannot be null");
			}if (returnValue==4) {
				throw new Error("ERROR: Upload Date cannot be null");
			}
			if (returnValue==5) {
				throw new Error("ERROR: Invalid Upload Date");
			}
			if (returnValue==6) {
				throw new Error("ERROR: Video already added");
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//add entry to videoGenres
		Boolean result=this.addVideoGenres(YouTubeID, contentType, contentName);
		if (result) {
			System.out.println("Video Added");
			return true;
		}
		return false;
	}

	public ArrayList<String> getVideos() {
//		//TODO:
		ArrayList<String> timestamps = new ArrayList<String>();
		
		return timestamps;
	}
	
	public boolean addVideoGenres(String YouTubeID, int ContentType, String ContentName) {
		//TODO:
		Connection con=this.dbHandler.getConnection();
		try {
			CallableStatement proc =con.prepareCall("{?=call dbo.InsertIntoVideoGenres(?,?,?)}");
			proc.setString(2, YouTubeID);
			proc.setInt(3, ContentType);
			proc.setString(4, ContentName);
			proc.registerOutParameter(1,Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			System.out.println(proc.getString(1));
			proc.close();
			if (returnValue==1) {
				throw new Error("ERROR: Content ID cannot be null");
			}
//			if (returnValue==2) {
//				throw new Error("ERROR: ContentType value does not exist");
//			}
			if (returnValue==3) {
				throw new Error("ERROR: VideoID cannot be null");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Video entry added to VideoGenres");
		return true;
	}
	
	public boolean createContentType(int ContentTypeID, String title) {
		//DONE: Creates a new contentType may want a better solution for IDs
		Connection con=this.dbHandler.getConnection();
		try {
			CallableStatement proc =con.prepareCall("{?=call dbo.AddContentType(?,?,?)}");
			proc.setInt(2, ContentTypeID);
			proc.setString(3, title);
			proc.setString(4,null);
			proc.registerOutParameter(1,Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			System.out.println(proc.getString(1));
			proc.close();
			
			if (returnValue==1) {
				throw new Error("ERROR: ContentTypeID cannot be null");
			}
			if (returnValue==2) {
				throw new Error("ERROR: ContentTypeTitle cannot be null");
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(title+" content type created");
		return true;
	}
	
}
