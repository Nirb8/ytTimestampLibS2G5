package ytTimestampLibS2G5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionHandler {

	//DO NOT EDIT THIS STRING, YOU WILL RECEIVE NO CREDIT FOR THIS TASK IF THIS STRING IS EDITED
	private final String SampleURL = "jdbc:sqlserver://${dbServer};databaseName=${dbName};user=${user};password={${pass}}";

	private Connection connection = null;

	private String databaseName;
	private String serverName;

	public DatabaseConnectionHandler(String serverName, String databaseName) {
		//DO NOT CHANGE THIS METHOD
		this.serverName = serverName;
		this.databaseName = databaseName;
	}

	public boolean connect(String user, String pass) {
		//TODO: Task 1
		//BUILD YOUR CONNECTION STRING HERE USING THE SAMPLE URL ABOVE
		String url = "jdbc:sqlserver://${dbServer};databaseName=${dbName};user=${user};password={${pass}}";
		
		String fullUrl = url
				.replace("${dbServer}", serverName)
				.replace("${dbName}", databaseName)
				.replace("${user}", user)
				.replace("${pass}", pass);
		
		try {
			Connection connection = DriverManager.getConnection(fullUrl);
			
			this.connection = connection;
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

	public Connection getConnection() {
		return this.connection;
	}
	
	public void closeConnection() {
		//TODO: Task 1
		try {
			this.connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
