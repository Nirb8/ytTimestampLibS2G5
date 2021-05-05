import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ytTimestampLibS2G5.AuthHandler;
import ytTimestampLibS2G5.DatabaseConnectionHandler;
import ytTimestampLibS2G5.TimestampService;
import ytTimestampLibS2G5.VideoService;

import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.*;

public class DataImport {

	public static void main(String[] args) {
		
		//taken from 
		DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler(
				"titan.csse.rose-hulman.edu", "ytTimestampLib_S2G5");
		AuthHandler authHandler = new AuthHandler(dbHandler);
		VideoService videoService = new VideoService(dbHandler);
		TimestampService timestampService = new TimestampService(dbHandler);

        // "simple" loop to continually read from console and call a switch statement
        Scanner s = new Scanner(System.in);
        boolean runStatus = true;
        
        System.out.println(".json file to import:");
		String importFrom = s.nextLine();
		
		JSONParser psr = new JSONParser();

		Pattern pat = Pattern.compile("<a href=\"https:\\/\\/www\\.youtube\\.com\\/watch\\?v=(.*)&amp;t=(.*)\">.*<\\/a>\\s(.*)");

		ArrayList<TagData> tdList = new ArrayList<TagData>();
		try {
			Object obj = psr.parse(new FileReader(importFrom));
			
			JSONArray comments = (JSONArray) obj;
			
			Iterator<JSONObject> iter = comments.iterator();

			while (iter.hasNext()) {
				JSONObject current = iter.next();
				String currentString = (String) current.get("commentText");
				String[] currentTags = currentString.split("<br />");
				//System.out.println(current.get("commentText"));
				for (String currentTag : currentTags) {
					//System.out.println(currentTags[i]);
					Matcher matcher = pat.matcher(currentTag);
					if (matcher.find()) {
						//System.out.println(matcher.group(0));
						//System.out.println(matcher.group(1));
						String youtubeID = matcher.group(1);
						
						//System.out.println(matcher.group(2));
						String time = timeConversionToHHMMSS(matcher.group(2));
						
						String title = StringEscapeUtils.unescapeHtml4(matcher.group(3));
						
						TagData td = new TagData(youtubeID, time, title);
						tdList.add(td);
						//System.out.println(StringEscapeUtils.unescapeHtml4(matcher.group(3)));
					}

				}
				
				
				
				
			}
			
			for(TagData td : tdList) {
				System.out.println(td.toString());
			}
			System.out.println("The listed tags will added to the database. Proceed? (y/n)");
			
			String response = s.nextLine();
			if(response.equalsIgnoreCase("y")) {
				System.out.println("Adding to database...");
				String importUserID = "fa71a859-0977-482d-87d1-363a342ba099";
				for(TagData td : tdList) {
							
					timestampService.addTimeStamp(importUserID, td.title, td.time, td.youtubeID);
				}
				System.out.println("Done!");
			} else {
				System.out.println("The import was cancelled.");
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		
	}

	
	
	
	
	
//	System.out.println(timeConversionToHHMMSS("1h23m45s"));     
//	System.out.println(timeConversionToHHMMSS("23m45s"));  
//	System.out.println(timeConversionToHHMMSS("03m45s")); 
//	System.out.println(timeConversionToHHMMSS("0m45s"));  
//	System.out.println(timeConversionToHHMMSS("10h23m45s"));  
	//test code
	/*
	 * Converts from XhXXmXXs or XXmXXs or XmXXs to hh:mm:ss
	 * */
	public static String timeConversionToHHMMSS(String time) {
		// TODO: using a string builder would increase performance, but it's optional
		switch(time.length()) {
		case 5: 
			time = "0" + time;
		case 6:
			time = "00:" + time;
			time = time.replace('s',' ');
			time = time.replace('m', ':');
			time = time.trim();
			break;
		case 8:
			time = "0" + time;
		case 9:
			time = time.replace('s',' ');
			time = time.replace('m', ':');
			time = time.replace('h', ':');
			time = time.trim();
			break;
		
	}
		return time;
	}
}
