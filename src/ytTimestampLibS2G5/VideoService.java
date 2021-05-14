package ytTimestampLibS2G5;

import SpecialClasses.TableList;
import YouTubeAPI.DBConnect;
import YouTubeAPI.DBConnect.VideoDetails;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class VideoService {
	
	private static final Random RANDOM = new SecureRandom();
	private DatabaseConnectionHandler dbHandler;
	
	public VideoService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	//adds a video to the table
	public boolean addVideo(String videoID) {
		DBConnect youtube_binfo = new DBConnect();
    	try {
    		VideoDetails vd = youtube_binfo.getYouTubeVideoDetails(videoID);
    		String durationTime = vd.getDuration();
    		String videoTitle = vd.getTitle();
    		Date uploadDate = new Date(vd.getPublishedDate().getValue());
    		int videoContentID = Integer.parseInt(vd.getContentType());
    		String contentName = vd.getContentName();
    		Time duration = Time.valueOf(durationTime);
    		Connection con = this.dbHandler.getConnection();
    		try {
    			CallableStatement proc = con.prepareCall("{? = call dbo.AddVideo(?,?,?,?)}");
    			proc.setString(2,videoID);
    			proc.setString(3, videoTitle);
    			proc.setTime(4, duration);
    			proc.setDate(5, uploadDate);
    			proc.registerOutParameter(1, Types.INTEGER);
    			proc.execute();
    			int returnValue = proc.getInt(1);
    			proc.close();
    			if (returnValue == 1) {
    				throw new Error("ERROR: VideoID cannot be null");
    			}
    			if (returnValue == 2) {
    				throw new Error("ERROR: Video Title cannot be null");
    			}
    			if (returnValue == 3) {
    				throw new Error("ERROR: Duration time cannot be null");
    			}if (returnValue == 4) {
    				throw new Error("ERROR: Upload Date cannot be null");
    			}
    			if (returnValue == 5) {
    				throw new Error("ERROR: Invalid Upload Date");
    			}
    			if (returnValue == 6) {
    				throw new Error("ERROR: Video already added");
    			}
    			//add entry to videoGenres
    			boolean result = this.addVideoGenres(videoID, videoContentID, contentName);
    			if (result) {
    				System.out.println("Video Added");
    				
    			}
    		}catch(Exception e) {
    			e.printStackTrace();
    			return false;
    		}
			
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	//returns an output of all the videos in the database
	public void getVideos(String contentTypeID) {
		//DONE: Shows a table of videos already added
		ArrayList<ArrayList<String>> videos = new ArrayList<>();
		Connection con= this.dbHandler.getConnection();
		int ID;
		if (contentTypeID==null) {
			ID=-1;
		}
		else{ID= Integer.parseInt(contentTypeID);}
		
		try {
			String query;
			if (ID==-1) {
				query="SELECT * FROM [dbo].[GetVideos]() ";
			}else {
				query="SELECT * FROM [dbo].[GetVideos]() WHERE ContentID=? ";
			}
			PreparedStatement prpstmt = con.prepareStatement(query);
			if (ID!=-1) {
			prpstmt.setInt(1,ID);
			}
			ResultSet rs =prpstmt.executeQuery();
			
			int count =1;
			while (rs.next()) {
				
					String videoTitle =rs.getString(1);
					String uploadDate = rs.getDate(2).toString();
					String duration = rs.getTime(3).toString();
					ArrayList<String> details = new ArrayList<>();
					details.add(String.valueOf(count));
					details.add(videoTitle);
					details.add(uploadDate);
					details.add(duration);
					if (ID==-1) {
					String content = rs.getString(4);
					details.add(content);
					}
					count++;
					if (!videos.contains(details)) {
					videos.add(details);
					}
					
			}
			if (ID==-1) {
				this.outputVideosWithContent(videos);
			}
			else {
				this.outputVideos(videos);
			}
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//ouput the videos if no content is selected for the search
	public void outputVideosWithContent(ArrayList<ArrayList<String>> results) {
		TableList table = new TableList(5,"Entry Number","Video Name", "Upload Date","Duration", "Content Type Name");
		results.forEach(element -> table.addRow(element.get(0), element.get(1), element.get(2), element.get(3), element.get(4)));
	
		table.print();
	}
	//output the videos if content is selected
	public void outputVideos(ArrayList<ArrayList<String>> results) {
		TableList table = new TableList(4,"Entry Number","Video Name", "Upload Date","Duration");
		for (ArrayList<String>element:results) {
			table.addRow(element.get(0), element.get(1), element.get(2), element.get(3));
		}
		table.print();
	}
	//adds a video to the videoGenres table
	public boolean addVideoGenres(String YouTubeID, int ContentType, String ContentName) {
		//DONE
		Connection con = this.dbHandler.getConnection();
		try {
			CallableStatement proc = con.prepareCall("{? = call dbo.InsertIntoVideoGenres(?,?,?)}");
			proc.setString(2, YouTubeID);
			proc.setInt(3, ContentType);
			proc.setString(4, ContentName);
			proc.registerOutParameter(1,Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			proc.close();
			if (returnValue == 1) {
				throw new Error("ERROR: Content ID cannot be null");
			}
//			if (returnValue == 2) {
//				throw new Error("ERROR: ContentType value does not exist");
//			}
			if (returnValue == 3) {
				throw new Error("ERROR: VideoID cannot be null");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Video entry added to VideoGenres");
		return true;
	}
	
	
	
}
