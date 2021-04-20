import java.util.Scanner;

public class Main {
	
	public static void main(String args[]) {
		// initialize new class instances here:
		// ex: AuthHandler authHandler = new AuthHandler();
		// such that we can call AuthHandler.login(username, password)
		
		
		// "simple" loop to continually read from console and call a switch statement
		
		Scanner s = new Scanner(System.in);
		
		boolean runStatus = true;
		while(runStatus) {
			
			String input = s.nextLine();
			//expect this switch statement to get CHONKY
			switch(input) {
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
				System.out.println("The command " + input + " is not recognized. Type h for help, or e for exit.");
			
			}
		}
		
		
	}
}
