import ytTimestampLibS2G5.AuthHandler;
import ytTimestampLibS2G5.DatabaseConnectionHandler;

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

            String input = s.nextLine();
            //expect this switch statement to get CHONKY
			/*
			* cases: h/help, e/x/exit/q/quit, l/login, logout, view/view tags, create/create tag, history
			*/
            switch (input) {
            	case "r":
            	case "register":
            		//prompt for registeration
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
                case "h":
                case "help":
                    System.out.println("\n\nDisplaying help commands:");
                    // add help commands here
                    break;
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

    }
}
