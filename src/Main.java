import ytTimestampLibS2G5.AuthHandler;
import ytTimestampLibS2G5.DatabaseConnectionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // initialize new class instances here:
        // such that we can call AuthHandler.login(username, password)

		DatabaseConnectionHandler dbHandler = new DatabaseConnectionHandler(
				"titan.csse.rose-hulman.edu", "ytTimestampLib_S2G5");
		AuthHandler authHandler = new AuthHandler(dbHandler);

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
				case "l":
				case "login":
					// prompt for username and password
					System.out.println("ENTER Username");
            		String loginusername =s.nextLine();
            		System.out.println("ENTER Password");
            		String loginpassword = s.nextLine();
					authHandler.login(loginusername, loginpassword);
					break;
                case "h":
                case "help":
                    System.out.println("\n\nDisplaying help commands:");
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
//                case "v":
//                	//temporary demo command
//				URL test;
//				try {
//					test = new URL("https://www.youtube.com/watch?v=MvaMY_92T-c");
//					System.out.println(test);
//				} catch (MalformedURLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//					
//                	break;
                case "e":
                case "x":
                case "exit":
                    System.out.println("Exiting...");
                    //probably want to close out connection to database and other stuff to end gracefully
                    runStatus = false; // break out of the while loop
                    break;
                default:
                    System.out.println("The command \"" + input + "\" is not recognized. Type h for help, or e for exit.");
            }
        }
        
        s.close();

    }
}
