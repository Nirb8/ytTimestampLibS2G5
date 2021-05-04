import ytTimestampLibS2G5.AuthHandler;
import ytTimestampLibS2G5.DatabaseConnectionHandler;
import ytTimestampLibS2G5.TimestampService;
import ytTimestampLibS2G5.VideoService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import YouTubeAPI.DBConnect;
import YouTubeAPI.DBConnect.VideoDetails;

public class Main {
	//test
    public static void main(String[] args) {
		DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler(
				"titan.csse.rose-hulman.edu", "ytTimestampLib_S2G5");
		AuthHandler authHandler = new AuthHandler(dbHandler);
		VideoService videoService = new VideoService(dbHandler);
		TimestampService timestampService = new TimestampService(dbHandler);

        // "simple" loop to continually read from console and call a switch statement
        Scanner s = new Scanner(System.in);
        boolean runStatus = true;
        while (runStatus) {

        	System.out.print("~ ");
            String input = s.nextLine();
            //expect this switch statement to get CHONKY
			/*
			* cases: h/help, e/x/exit/q/quit, l/login, logout, view/view tags, create/create tag, history
			*/
            //test comment
            switch (input) {
            	case "r":
            	case "register":
            		//prompt for registration
            		System.out.println("Adding New User");
            		System.out.println("ENTER Username");
            		String username =s.nextLine();
            		System.out.println("ENTER Password");
            		String password = s.nextLine();
            		authHandler.register(username,password);
            		break;
				case "l":
				case "login":
					// prompt for username and password
					System.out.println("ENTER Username");
            		String loginusername =s.nextLine();
            		System.out.println("ENTER Password");
            		String loginpassword = s.nextLine();
					authHandler.login(loginusername, loginpassword);
					break;		
//					
//				case "ct":
//				case "new content":
//					System.out.println("Enter ContentID (number)");
//					int contentID = Integer.valueOf(s.nextLine());
//					System.out.println("Enter name of content type");
//					String contentTitle = s.nextLine();
//					videoService.createContentType(contentID, contentTitle);
//					break;
//					
//				case "cv":
//				case "add video":
//					System.out.println("Enter YouTube VideoID");
//					String videoID = s.nextLine();
//					videoService.addVideo(videoID);
//			    	break;
				case "ct":
				case "create Timestamp":
					System.out.println("Enter YouTube VideoID");
					String timestampVideoID = s.nextLine();
					System.out.println("Enter Time (hh:mm:ss)");
					String timestampTime = s.nextLine();
					System.out.println("Enter caption");
					String caption =s.nextLine();
					String id =authHandler.getCurrentUser();
					timestampService.addTimeStamp(id,caption, timestampTime, timestampVideoID);
					break;
				case "g":
				case "get Timestamp":
					System.out.println("Enter YouTube VideoID if you want to search");
					String getTimestampVideoID =s.nextLine();
					ArrayList<ArrayList<String>> results =timestampService.getTimestamps(getTimestampVideoID);
					timestampService.outputConsoleTables(results);
					break;
				case "d":
				case "delete Timestamp":
					System.out.println("Enter the timestamp's video ID that you want to delete");
					String deleteVideoID =s.nextLine();
					System.out.println("Enter the timestamps' video time that you want to delete (hh:mm:ss)");
					String deleteVideoTime=s.nextLine();
					String userID = authHandler.getCurrentUser();
					timestampService.deleteTimestamp(deleteVideoID,deleteVideoTime, userID);
					break;
				case "h":
                case "help":
                    System.out.println("\nDisplaying help commands:");
                    File helpFile = new File("help.txt");
					try {
						Scanner helpReader = new Scanner(helpFile);
						while(helpReader.hasNextLine()) {
							System.out.println(helpReader.nextLine());
						}
						helpReader.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
                    break;
//              case "v":
//                	//temporary demo command
//					URL test;
//					try {
//						test = new URL("https://www.youtube.com/watch?v=MvaMY_92T-c");
//						System.out.println(test);
//					} catch (MalformedURLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//                	break;
                case "e":
                case "x":
				case "q":
				case "quit":
                case "exit":
                    System.out.println("Exiting...");
                    //probably want to close out connection to database and other stuff to end gracefully
					dbHandler.closeConnection();
                    runStatus = false; // break out of the while loop
                    break;
                default:
                    System.out.println("The command \"" + input + "\" is not recognized. Type h for help, or e for exit.");
            }
        }
        
        s.close();
    }
}
