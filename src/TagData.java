
public class TagData {
	String time;
	String youtubeID;
	String title;
	
	public TagData(String youtubeID,String time, String title) {
		this.youtubeID = youtubeID;
		this.time = time;
		this.title = title;
	}
	
	public String toString() {
		return "ytID: " + youtubeID + ", time: " + time + ", title: " + title;
	}
}
