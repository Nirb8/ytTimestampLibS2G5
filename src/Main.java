
import ytTimestampLibS2G5.AuthHandler;
import ytTimestampLibS2G5.ContentTypeService;
import ytTimestampLibS2G5.DatabaseConnectionHandler;
import ytTimestampLibS2G5.TimestampService;
import ytTimestampLibS2G5.TimestampService.NumberRowsCollection;
import ytTimestampLibS2G5.VideoService;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import YouTubeAPI.RegexConverter;

public class Main {
	//test
    public static void main(String[] args) {
		DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler(
				"titan.csse.rose-hulman.edu", "ytTimestampLib_S2G5");
		AuthHandler authHandler = new AuthHandler(dbHandler);
		VideoService videoService = new VideoService(dbHandler);
		TimestampService timestampService = new TimestampService(dbHandler);
		ContentTypeService contentTypeService = new ContentTypeService(dbHandler);

        // "simple" loop to continually read from console and call a switch statement
        Scanner s = new Scanner(System.in);
        boolean runStatus = true;
        boolean signIn = false;
        while (runStatus) {
        	
        	
            //expect this switch statement to get CHONKY
            while (!signIn) {
            	System.out.println("Please sign in: Press r to register or l to login");
            	System.out.print("~ ");
                String input = s.nextLine();
				switch (input) {
				case "cv":
				case "add video":
					System.out.println("Enter YouTube VideoID");
					String videoID = s.nextLine();
					videoService.addVideo(videoID);
			    	break;
					case "r":
					case "register":
						//prompt for registration
						System.out.println("Adding New User");
						System.out.println("ENTER Username");
						String username = s.nextLine();
						System.out.println("ENTER Password");
						String password = s.nextLine();
						boolean result1 = authHandler.register(username,password);
						if (result1) {
							signIn = true;
						}
						break;
					case "l":
					case "L":
					case "login":
					case "LOGIN":
						// prompt for username and password
						System.out.println("ENTER Username");
						String loginusername = s.nextLine();
						System.out.println("ENTER Password");
						String loginpassword = s.nextLine();
						boolean result = authHandler.login(loginusername, loginpassword);
						if (result) {
							signIn = true;
						}
						break;
					case "e":
					case "x":
					case "q":
					case "quit":
					case "exit":
					case "QUIT":
					case "EXIT":
					case "E":
					case "X":
					case "Q":
					case "ZZ": //for you vim users out there
							System.out.println("Exiting...");
							//probably want to close out connection to database and other stuff to end gracefully
							dbHandler.closeConnection();
							s.close();
							return;
					case "":
							break;
					default:
							System.out.println("The command \"" + input + "\" is not recognized. Type h for help, or e for exit.");
				}
            }
				
            System.out.print("~ ");
            String input = s.nextLine();
            switch(input) {
            	case "":
            		break;
				case "ct":
				case "CT":
				case "create Timestamp":
					System.out.println("Enter YouTube VideoID");
					String timestampVideoID = RegexConverter.convertLinkToYTVideoID(s.nextLine());
					System.out.println("Enter Time (hh:mm:ss)");
					String timestampTime = s.nextLine();
					System.out.println("Enter caption");
					String caption = s.nextLine();
					String id = authHandler.getCurrentUser();
					timestampService.addTimeStamp(id,caption, timestampTime, timestampVideoID);
					break;
				case "f":
					System.out.println("Favorite Timestamp Search");
					ArrayList<ArrayList<String>>result =timestampService.getFavoriteTimestamps(authHandler.getCurrentUser());
					timestampService.outputConsoleTables(result);
					break;
				case "g":
				case "G":
				case "get Timestamp":
					System.out.println("Press v to search by video ID, c to search by content type, or any other key to cancel");
					System.out.print("~ ");
					String input3 = s.nextLine();
					switch(input3) {
					case "v":
					case "V":
						System.out.println("Enter YouTube VideoID if you want to search");
						String getTimestampVideoID;
						try {
							getTimestampVideoID = RegexConverter.convertLinkToYTVideoID(s.nextLine());
						} catch(IllegalArgumentException e) {
							System.out.println(e.getMessage());
							break;
						}
						ArrayList<ArrayList<String>> results;
						if (!getTimestampVideoID.isEmpty()) {
							results= timestampService.getTimestampsByVideoID(getTimestampVideoID, authHandler.getCurrentUser());
						}
						else {
							results = timestampService.getTimestamps(authHandler.getCurrentUser());
						}
						
						timestampService.frontEndSelectionFunction(results, s, authHandler);
					
						break;
					case "c":
						ArrayList<ArrayList<String>> contentResults=contentTypeService.getContentTypes();
						contentTypeService.outputContent(contentResults);
			            System.out.println("Press the entry number that you want to select or press enter for default search");
			            System.out.print("~ ");
			            String num = s.nextLine();
			            
			            ArrayList<String> selectedRow;
						String getContentTypeID;
			            try {
			            	if (!num.isEmpty()) {
							selectedRow = contentResults.get(Integer.parseInt(num)-1);
							getContentTypeID= selectedRow.get(1);
			            	}else {
							getContentTypeID= authHandler.getFavoriteContent();
			            	}
						} catch (NumberFormatException | IndexOutOfBoundsException e) {
							System.out.println("This is not a valid number.");
							break;
						}
						ArrayList<ArrayList<String>> results2= timestampService.searchTimestampsByType(getContentTypeID, authHandler.getCurrentUser());
						timestampService.frontEndSelectionFunction(results2, s, authHandler);
						break;
					default:
						System.out.println("Cancelling search...");
						break;
					}
					
					break;
				case "gv":
				case "get videos":
					ArrayList<ArrayList<String>> contentResults2=contentTypeService.getContentTypes();
					contentTypeService.outputContent(contentResults2);
		            System.out.println("Press the entry number that you want to search or press enter for basic search");
		            System.out.print("~ ");
		            String num2 = s.nextLine();
		            String getContentTypeID2;
		            if (!num2.isEmpty()) {
		            ArrayList<String> selectedRow = contentResults2.get(Integer.parseInt(num2)-1);
		            getContentTypeID2= selectedRow.get(1);
		            } else {
		            	getContentTypeID2=null;
		            }
		            videoService.iterateThroughVideos(getContentTypeID2, s);
					break;
				case "vh":
				case "view user history":
					ArrayList<ArrayList<String>> history = timestampService.getUserHistory(authHandler.getCurrentUser());
					timestampService.iterateThroughHistory(history, s);
					break;
				case "gm":
				case "get my Timestamps":
					timestampService.frontEndGetMyTimestamps(authHandler, s);
					break;
				case "p":
				case "profile":
					authHandler.frontEndProfile(s, contentTypeService);
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
