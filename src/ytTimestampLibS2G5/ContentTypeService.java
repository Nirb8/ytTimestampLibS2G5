package ytTimestampLibS2G5;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import SpecialClasses.TableList;

public class ContentTypeService {
	private DatabaseConnectionHandler dbHandler;
	
	public ContentTypeService(DatabaseConnectionHandler dbHandler) {
		this.dbHandler = dbHandler;
	}
	//procedure completed in SQL stored prodedure insert into videoGenres
//	public boolean createContentType(int ContentTypeID, String title) {
//		//DONE: Creates a new contentType may want a better solution for IDs
//		Connection con = this.dbHandler.getConnection();
//		try {
//			CallableStatement proc = con.prepareCall("{? = call dbo.AddContentType(?,?,?)}");
//			proc.setInt(2, ContentTypeID);
//			proc.setString(3, title);
//			proc.setString(4,null);
//			proc.registerOutParameter(1,Types.INTEGER);
//			proc.execute();
//			int returnValue = proc.getInt(1);
//			System.out.println(proc.getString(1));
//			proc.close();
//			
//			if (returnValue == 1) {
//				throw new Error("ERROR: ContentTypeID cannot be null");
//			}
//			if (returnValue == 2) {
//				throw new Error("ERROR: ContentTypeTitle cannot be null");
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		System.out.println(title+" content type created");
//		return true;
//	}
	
	public ArrayList<ArrayList<String>> getContentTypes() {
		Connection con = this.dbHandler.getConnection();
		ArrayList<ArrayList<String>> contentTypes = new ArrayList<>();
		String query ="SELECT ContentTypeID, ContentTypeTitle FROM ContentTypes Order By ContentTypeID asc";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs= stmt.executeQuery(query);
			int count=1;
			while (rs.next()) {
				String id = rs.getString(1);
				String name = rs.getString(2);
				ArrayList<String> details = new ArrayList<String>();
				details.add(String.valueOf(count));
				details.add(id);
				details.add(name);
				contentTypes.add(details);
				count++;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		return contentTypes;
	}
	
	public void outputContent(ArrayList<ArrayList<String>> results) {
		TableList table = new TableList(3,"Entry Number", "Reference ID Number", "Content Type Name");
		results.forEach(element->table.addRow(element.get(0),element.get(1),element.get(2)));
		table.print();
	}
	
	
}
