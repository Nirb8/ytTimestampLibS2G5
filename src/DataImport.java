import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

public class DataImport {

	public static void main(String[] args) {
		
		
		JSONParser psr = new JSONParser();
		
		try {
			Object obj = psr.parse(new FileReader("input.json"));
			
			JSONArray comments = (JSONArray) obj;
			
			
			
			
			
			Iterator<JSONObject> i = comments.iterator();
			
			while(i.hasNext()) {
				JSONObject current = i.next();
				
				System.out.println(current);
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
