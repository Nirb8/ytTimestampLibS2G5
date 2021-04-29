import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.*;

public class DataImport {

	public static void main(String[] args) {
		JSONParser psr = new JSONParser();

		Pattern pat = Pattern.compile("<a href=\"https:\\/\\/www\\.youtube\\.com\\/watch\\?v=(.*)&amp;t=(.*)\">.*<\\/a>\\s(.*)");

		try {
			Object obj = psr.parse(new FileReader("input.json"));
			
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
						System.out.println(matcher.group(1));
						System.out.println(matcher.group(2));
						System.out.println(StringEscapeUtils.unescapeHtml4(matcher.group(3)));
					}

				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

}
