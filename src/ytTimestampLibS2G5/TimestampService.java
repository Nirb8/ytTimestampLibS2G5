package ytTimestampLibS2G5;

import SpecialClasses.TableList;
import javafx.animation.KeyValue.Type;
import ytTimestampLibS2G5.TimestampService.NumberRowsCollection;

import com.google.api.client.util.DateTime;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class TimestampService {
	
	private static final Random RANDOM = new SecureRandom();
	
	private DatabaseConnectionHandler dbHandler;
	
	public TimestampService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	
	//adds new timestamps by 
	public boolean addTimeStamp(String Id,String caption, String videoTime, String videoId) {
		//DONE: Complete this method.
		String current = LocalDate.now().toString();
		DateTime time = new DateTime(current);
		Date uploadDate = new Date(time.getValue());
		
		try {
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
				System.out.println("ERROR: VideoID cannot be null");
			}
			if (returnValue==3) {
				System.out.println("ERROR: Time input is invalid");
			}
			if (returnValue==4) {
				System.out.println("ERROR: Timestamp time greater than duration");
			}
			if (returnValue==5) {
				throw new Error("ERROR: TimestampID already in use");
			}
			
			
			if (returnValue==0) {
				System.out.println("Timestamp Entry Added");
				boolean result = createTimestampTag(uniqueID,videoId);
				if (result) {
					System.out.println("TimestampTag created");
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}catch (Exception e) {
			System.out.println("ERROR: Time cannot be null");
		}
		return false;
	}
	
	//search Timestamps by content Type
	public ArrayList<ArrayList<String>> searchTimestampsByType(String content, String accessingUserID){
		System.out.println("Specified Search by Content");
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> timestamps = new ArrayList<>();
		//String query="select * from [dbo].GetTimestampsByContentType(?) ORDER BY [YouTube ID] asc ,[Timestamp Time] asc"; //can't figure out how to ORDER BY these in the function
		try {
//			PreparedStatement prpstmt = con.prepareStatement(query);
//			prpstmt.setInt(1, Integer.valueOf(content));
//			ResultSet rs=prpstmt.executeQuery();
			CallableStatement cstmt = con.prepareCall("{? = call GetTimestampsByContentType(?)}");
			
			cstmt.registerOutParameter(1, Types.INTEGER);
			
			cstmt.setInt(2, Integer.valueOf(content));
			
			ResultSet rs = cstmt.executeQuery();
			
			int count=0;
			while (rs.next()) {
				String ID = rs.getString(1);
				String name = rs.getString(2);
				String des = rs.getString(3);
				String tTime = rs.getTime(4).toString();
				String cType = rs.getString(5);
				String cTime = rs.getDate(6).toString();
				String UserName = rs.getString(7);
				String tID = rs.getString(8);
				ArrayList<String> details = new ArrayList<>();
				count++;
				details.add(String.valueOf(count));
				details.add(ID);
				details.add(name);
				details.add(des);
				details.add(tTime);
				details.add(cType);
				details.add(cTime);
				details.add(UserName);
				details.add(tID);
				timestamps.add(details);
				this.addTimestampToUserHistory(accessingUserID, tID);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
		return timestamps;
	}
	
	//Basic grab of all timestamps
	public ArrayList<ArrayList<String>> getTimestamps(String accessingUserID) {
		//DONE:Shows all the timestamps attached to a video
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> timestamps = new ArrayList<>();
		
				System.out.println("Basic Search");
				//query ="SELECT * FROM [dbo].[GetAllTimestamps]() ORDER BY [YouTube ID] asc, [Timestamp Time] asc";
				
			
			try {
				//Statement stmt = con.createStatement();
				//ResultSet rs=stmt.executeQuery(query);
				CallableStatement cstmt = con.prepareCall("{? = call GetAllTimestamps()}");
				cstmt.registerOutParameter(1, Types.INTEGER);
				
				ResultSet rs = cstmt.executeQuery();
				
				int count=0; 
				while (rs.next()) {
					String ID = rs.getString("YouTube ID");
					String name = rs.getString("Video Name");
					String des = rs.getString("Description");
					String tTime = rs.getTime("Timestamp Time").toString();
					String cType = rs.getString("Content Type");
					String cTime = rs.getDate("Created Time").toString();
					String UserName = rs.getString("Creator");
					String tID = rs.getString("TimestampID");
					ArrayList<String> details = new ArrayList<>();
					count++;
					details.add(String.valueOf(count));
					details.add(ID);
					details.add(name);
					details.add(des);
					details.add(tTime);
					details.add(cType);
					details.add(cTime);
					details.add(UserName);
					details.add(tID);
					//System.out.println("Adding entry: " + details.toString() + " to results.");
					timestamps.add(details);
					this.addTimestampToUserHistory(accessingUserID, tID);
				}
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return null;
			}
			return timestamps;
	}
	
	//search Timestamps by video ID
	public ArrayList<ArrayList<String>> getTimestampsByVideoID(String videoID, String accessingUserID) {
		System.out.println("Specified Search");
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> timestamps = new ArrayList<>();
			try {
				CallableStatement cstmt = con.prepareCall("{? = call [dbo].[GetTimestampsByYouTubeID](?)}");
				cstmt.registerOutParameter(1, Types.INTEGER);
				cstmt.setString(2, videoID);
				
				ResultSet rs= cstmt.executeQuery();
				

				int count=0;
				while (rs.next()) {
					String ID = rs.getString("YouTube ID");
					String name = rs.getString("Video Name");
					String des = rs.getString("Description");
					String tTime = rs.getTime("Timestamp Time").toString();
					String cType = rs.getString("Content Type");
					String cTime = rs.getDate("Created Time").toString();
					String UserName = rs.getString("Creator");
					String tID = rs.getString("TimestampID");
					ArrayList<String> details = new ArrayList<>();
					count++;
					details.add(String.valueOf(count));
					details.add(ID);
					details.add(name);
					details.add(des);
					details.add(tTime);
					details.add(cType);
					details.add(cTime);
					details.add(UserName);
					details.add(tID);
					timestamps.add(details);
					this.addTimestampToUserHistory(accessingUserID, tID);
				}
				rs.close();
				//test code
				
				StringBuilder exportString = new StringBuilder();
				
				exportString.append("https://www.youtube.com/watch?v=").append(videoID).append("\n");
				
				for(ArrayList<String> d:timestamps) {
					if(d.isEmpty()) {
						continue;
					}
					String des = d.get(3);
					String tTime = d.get(4);
				exportString.append(tTime).append(" ").append(des).append("\n");
				}
				
				StringSelection stringSelection = new StringSelection(exportString.toString());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return null;
			}
			return timestamps;
	}
		
	public ArrayList<ArrayList<String>> getFavoriteTimestamps(String accessingUserID){
		Connection con = this.dbHandler.getConnection();
		
		ArrayList<ArrayList<String>> timestamps = new ArrayList<>();
		
		try {
			CallableStatement cstmt = con.prepareCall("{? = call GetFavoriteTimestamps(?)}");
			
			cstmt.registerOutParameter(1, Types.INTEGER);
			
			cstmt.setString(2, accessingUserID);
			
			ResultSet rs = cstmt.executeQuery();
			
			int count=0; 
			while (rs.next()) {
				String ID = rs.getString("YouTube ID");
				String name = rs.getString("Video Name");
				String des = rs.getString("Description");
				String tTime = rs.getTime("Timestamp Time").toString();
				String cType = rs.getString("Content Type");
				String cTime = rs.getDate("Created Time").toString();
				String UserName = rs.getString("Creator");
				String tID = rs.getString("TimestampID");
				ArrayList<String> details = new ArrayList<>();
				count++;
				details.add(String.valueOf(count));
				details.add(ID);
				details.add(name);
				details.add(des);
				details.add(tTime);
				details.add(cType);
				details.add(cTime);
				details.add(UserName);
				details.add(tID);
				timestamps.add(details);
				
				this.addTimestampToUserHistory(accessingUserID, tID);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			return null;
		}
		
		return timestamps;
	}

	public ArrayList<ArrayList<String>> getUserHistory(String accessingUserID) {
		//DONE:Shows all the timestamps attached to a video
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> history = new ArrayList<>();
		try {
			CallableStatement cstmt = con.prepareCall("{? = call GetUserHistory(?)}");
			cstmt.registerOutParameter(1, Types.INTEGER);
			
			cstmt.setString(2, accessingUserID);
			ResultSet rs= cstmt.executeQuery();
			int count=0;
			while (rs.next()) {
				String ID = rs.getString("YouTube ID");
				String name = rs.getString("Video Name");
				String des = rs.getString("Description");
				String tTime = rs.getTime("Timestamp Time").toString();
				String uTime = rs.getTimestamp("Time Accessed").toString().substring(0, 19);
				ArrayList<String> details = new ArrayList<>();
				count++;
				details.add(String.valueOf(count));
				details.add(ID);
				details.add(name);
				details.add(des);
				details.add(tTime);
				details.add(uTime);
				history.add(details);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		return history;
	}
	//pulls timestamps from the database
	public ArrayList<ArrayList<String>> getUsersTimestamps(String userName, String accessingUserID) {
		Connection con = this.dbHandler.getConnection();
		
		ArrayList<ArrayList<String>> timestamps = new ArrayList<>();

		int count=0;
			try {
				CallableStatement cstmt = con.prepareCall("{? = call GetTimestampsCreatedByUser(?)}");
				
				cstmt.registerOutParameter(1, Types.INTEGER);
				
				cstmt.setString(2, accessingUserID);

				ResultSet rs=cstmt.executeQuery();
				while (rs.next()) {
			
					String ID = rs.getString("YouTube ID");
					String name = rs.getString("Video Name");
					String des = rs.getString("Description");
					String tTime = rs.getTime("Timestamp Time").toString();
					String cType = rs.getString("Content Type");
					String cTime = rs.getDate("Created Time").toString();
					String UserName = rs.getString("Creator");
					String tID = rs.getString("TimestampID");
					ArrayList<String> details = new ArrayList<>();
					count++;
					details.add(String.valueOf(count));
					details.add(ID);
					details.add(name);
					details.add(des);
					details.add(tTime);
					details.add(cType);
					details.add(cTime);
					details.add(UserName);
					details.add(tID);
					timestamps.add(details);
					this.addTimestampToUserHistory(accessingUserID, tID);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return null;
			}
			return timestamps;	
	}
	
	//Used specifically for timestamp search output table
	public void outputConsoleTables(List<ArrayList<String>> results) {
		
		TableList table = new TableList(8,"Entry Number","YouTube ID", "Video Name", "Description", "Timestamp Time", "Content Type", "Created Time", "Creator");
		results.forEach(element -> table.addRow(element.get(0), element.get(1), element.get(2), element.get(3), element.get(4), element.get(5), element.get(6), element.get(7)));
		table.print();
	}

	//Used specifically for user history output table
	public void outputHistory(List<ArrayList<String>> results) {
		TableList table = new TableList(6,"Entry Number","YouTube ID", "Video Name", "Description", "Timestamp Time", "Time Accessed");
		results.forEach(element -> table.addRow(element.get(0), element.get(1), element.get(2), element.get(3), element.get(4), element.get(5)));
		table.print();
	}
	//Used for outputting select row for manipulation
	public void outputSelection(ArrayList<String> result) {
		TableList table = new TableList(8,"Entry Number","YouTube ID", "Video Name", "Description", "Timestamp Time", "Content Type", "Created Time", "Creator");
		table.addRow(result.get(0),result.get(1), result.get(2), result.get(3), result.get(4),result.get(5), result.get(6), result.get(7));
		table.print();
	}
	//creates a timestampTag in the table used when creating a timestamp
	public boolean createTimestampTag(String timestampID, String videoID) {
		Connection con=this.dbHandler.getConnection();
		String query="SELECT ContentTypeID FROM dbo.VideoGenres WHERE YTVideoID=?";
		try {
			PreparedStatement prpstmt = con.prepareStatement(query);
			prpstmt.setString(1,videoID);
			ResultSet rs = prpstmt.executeQuery();
			rs.next();
			String contentID = rs.getString("ContentTypeID");
			
			CallableStatement proc = con.prepareCall("{?=call dbo.InsertIntoTimestampTags(?,?)}");
			proc.setString(3,timestampID);
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
		System.out.println(e.getMessage());
		//System.out.println("Error");
		return false;
		}
		return true;
	}
	
	//deletes timestamp by selection row information
	public boolean deleteTimestamp(ArrayList<String> row, String userID) {
		//DONE: select by row for deletion
		Connection con = this.dbHandler.getConnection();
		String query = "DELETE FROM Timestamps WHERE AuthorID=? AND YTVideoID=? AND TimestampTitle=?";
		String query2="SELECT * FROM Timestamps WHERE AuthorID=? AND YTVideoID=? AND TimestampTitle=?";
		try {
			PreparedStatement prpsmt = con.prepareStatement(query2);
			prpsmt.setString(1, userID);
			prpsmt.setString(2, row.get(1));
			prpsmt.setString(3,row.get(3));
			ResultSet results =prpsmt.executeQuery();
			if (results.next()) {
			PreparedStatement prpsmt2 = con.prepareStatement(query);
			prpsmt2.setString(1, userID);
			prpsmt2.setString(2, row.get(1));
			prpsmt2.setString(3,row.get(3));
			prpsmt2.execute();
			
			}
			else {
				System.out.println("No deletion performed, no timestamp matches given parameters");
				return false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("No deletion performed");
			//e.printStackTrace();
			return false;
		}
		System.out.println("Timestamp Deleted");
		return true;
		
	}

	//delete timestamps by videoID another option CURRENTLY NOT IN USE
//	public boolean deleteVideoTimestamps(String videoID) {
//		//DONE: select by row for deletion
//		Connection con = this.dbHandler.getConnection();
//		String query = "DELETE FROM Timestamps WHERE YTVideoID=?";
//		String query2="SELECT * FROM Timestamps WHERE YTVideoID=?";
//		try {
//			PreparedStatement prpsmt = con.prepareStatement(query2);
//			prpsmt.setString(1, videoID);
//			ResultSet results =prpsmt.executeQuery();
//			if (results.next()) {
//				PreparedStatement prpsmt2 = con.prepareStatement(query);
//				prpsmt2.setString(1, videoID);
//				prpsmt2.execute();
//			}
//			else {
//				System.out.println("No deletion performed, no timestamp matches given parameters");
//				return false;
//			}
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//			System.out.println("No deletion performed");
//			//e.printStackTrace();
//			return false;
//		}
//		System.out.println("Timestamps Deleted");
//		return true;
//	}
	
	public int addTimestampToUserHistory(String userID, String timestampID) {
		CallableStatement cstmt;
		try {
			cstmt = this.dbHandler.getConnection().prepareCall("{? = call UpdateUserHistory(?, ?)}");
			
			cstmt.registerOutParameter(1, Types.INTEGER);
			if(userID == null || userID.isEmpty()) {
				//user doesn't know this is being called, so it shouldn't really print any errors
				//handle history entry not updating in getTimestamps
				return 1;
			} else {
				cstmt.setString(2, userID);
			}
			if(timestampID == null || timestampID.isEmpty()) {
				return 2;
			} else {
				cstmt.setString(3, timestampID);
			}
			
			cstmt.execute();

			return cstmt.getInt(1);
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			
		}
		
		return 0;
	}
	
	//modifies the content description of the timestamp
	public boolean updateTimestamps(ArrayList<String> row, String userID, String newTitle ) {
		//DONE: allow timestamps to be updated need to add userhistory updated
		Connection con = this.dbHandler.getConnection();
		try {
			CallableStatement proc = con.prepareCall("{?=call dbo.updateTimestamp(?,?,?)}");
//			System.out.println(row.get(row.size()-1));
			proc.setString(2, row.get(row.size()-1));
			proc.setString(3,newTitle);
			proc.setString(4, userID);
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			proc.close();
			if (returnValue==1) {
				throw new Error("ERROR: TimestampID cannot be null");
			}
			if (returnValue==2) {
				throw new Error("ERROR: User does not match author");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Timestamp Entry Updated");
		return false;
	}
	
	//adds a timestamp to the favorite table
	public boolean favoriteTimestamp(String UserID, String date, String videoID, String title, String time) {
		//DONE: add timestamps to favorite table
		Connection con = this.dbHandler.getConnection();
		try {
			CallableStatement proc=con.prepareCall("{?=call dbo.AddFavorite(?,?,?,?,?)}");
			proc.setString(2,UserID);
			proc.setDate(3, Date.valueOf(date));
			proc.setString(4, videoID);
			proc.setString(5, title);
			proc.setTime(6, Time.valueOf(time));
			proc.registerOutParameter(1, Types.INTEGER);
			proc.execute();
			int returnValue = proc.getInt(1);
			proc.close();
			if (returnValue==1) {
				throw new Error("ERROR: User cannot be null");
			}
			if (returnValue==2) {
				throw new Error("ERROR: Invalid inputs");
			}
			if (returnValue==0) {
				System.out.println("Favorite Timestamp Added");
			}
		}catch (SQLException e) {
			//e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	//outputs timestamps 20 rows at a time
	public NumberRowsCollection iterateThroughTimestamps(ArrayList<ArrayList<String>> results, Scanner s) {
		boolean state=false;
		int numEnters=0;
		List<ArrayList<String>> current=null;
		while (!state) {
			
			if (results.size()<20) {
				state=true;
				current = results;
			}
			else {
				current=results.subList(0, 20);
				results = new ArrayList<ArrayList<String>>(results.subList(20, results.size()));
			}
		this.outputConsoleTables(current);
		if (!state) {
			System.out.println("Press enter to continue searching or press any key to stop: "+results.size()+" entries not shown");
			String search = s.nextLine();
			if (!search.isEmpty()) {
				return new NumberRowsCollection(current,numEnters);
			}
			numEnters++;
		}
		
		}
		return new NumberRowsCollection(current,numEnters);
	}
	//helper class for iterateThroughTimestamps
	public class NumberRowsCollection{
		List<ArrayList<String>> rows;
		int numEnters;
		
		public NumberRowsCollection(List<ArrayList<String>> rows, int numEnters) {
			this.rows=rows;
			this.numEnters=numEnters;
		}
		
		public List<ArrayList<String>>getRows() {
			return this.rows;
		}
		
		public int getEnters() {
			return this.numEnters;
		}
		
		
	}
	//User input function for main for basic search
	public void frontEndSelectionFunction(ArrayList<ArrayList<String>> results, Scanner s, AuthHandler authHandler) {
		NumberRowsCollection collection =this.iterateThroughTimestamps(results,s);
		List<ArrayList<String>> current =collection.getRows();
		int numEnters = collection.getEnters();
		//single row selection
		System.out.println("Press s to select a timestamp or any other key to exit selection mode");
		System.out.print("~ ");
        String input5 = s.nextLine();
        switch(input5) {
        case "s":
        	System.out.println("Press the entry number that you want to select");
        	String num3 = s.nextLine();
        	int div;
        	if (numEnters>0) {
        		div=20*numEnters;
        	}
        	else {
        		div=1;
        	}
        	try {
        	ArrayList<String> selectedRow2 = current.get(Integer.parseInt(num3)/div-1);
        	this.outputSelection(selectedRow2);
        	System.out.println("Press f to favorite or any other key to exit");
        	System.out.print("~ ");
        	String query = s.nextLine();
        	switch(query) {
        	case "f":
        		this.favoriteTimestamp(authHandler.getCurrentUser(), selectedRow2.get(6),selectedRow2.get(1), selectedRow2.get(3), selectedRow2.get(4));
        		break;
        	case "e":
        	default:
        		System.out.println("Exiting Selection Mode...");
        		break;
        	}
        	}catch(Exception e) {
        		System.out.println("ERROR: Invalid Entry Number");
        	}
        case "e":
        default:
        	System.out.println("Exiting Selection Mode...");
        	break;
        }
	}
	//Used in Main to get user's timestamps
	public void frontEndGetMyTimestamps(AuthHandler authHandler, Scanner s) {
		String username = authHandler.getCurrentUserName();
		boolean runSelection = true;
		while (runSelection) {
			ArrayList<ArrayList<String>> myresults = this.getUsersTimestamps(username, authHandler.getCurrentUser());
			NumberRowsCollection collection =this.iterateThroughTimestamps(myresults,s);
			List<ArrayList<String>> current =collection.getRows();
			int numEnters = collection.getEnters();
			System.out.println("Press s to select a timestamp or e to exit selection mode");
			System.out.print("~ ");
            String input2 = s.nextLine();
            switch(input2) {
            case "s":
            	System.out.println("Press the entry number that you want to select");
            	String num = s.nextLine();
            	int div;
            	if (numEnters>0) {
            		div=20*numEnters;
            	}
            	else {
            		div=1;
            	}
            	ArrayList<String> selectedRow = current.get(Integer.parseInt(num)/div-1);
            	this.outputSelection(selectedRow);
            	System.out.println("Press d to delete, u to update, or e to exit");
            	System.out.print("~ ");
            	String query = s.nextLine();
            	String userId = authHandler.getCurrentUser();
            	switch(query) {
					case "e":
						runSelection = false;
						System.out.println("Exiting selection mode...");
						break;
					case "u":
						System.out.println("Enter new description");
						String newTitle = s.nextLine();
						this.updateTimestamps(selectedRow, userId, newTitle);
						break;
					case "d":
						this.deleteTimestamp(selectedRow,userId);
						break;
            	}
            	break;
            case "e":
            	runSelection = false;
            	System.out.println("Exiting selection mode...");
            	break;
            }
		}
	}
	
	//outputs history 20 rows at a time
		public void iterateThroughHistory(ArrayList<ArrayList<String>> results, Scanner s) {
			boolean state=false;
			List<ArrayList<String>> current=null;
			while (!state) {
				
				if (results.size()<20) {
					state=true;
					current = results;
				}
				else {
					current=results.subList(0, 20);
					results = new ArrayList<ArrayList<String>>(results.subList(20, results.size()));
				}
			this.outputHistory(current);
			if (!state) {
				System.out.println("Press enter to continue searching or press any key to stop: "+results.size()+" entries not shown");
				String search = s.nextLine();
				if (!search.isEmpty()) {
					state=true;
				}
			}
			
			}
		}
}
