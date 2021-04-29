package YouTubeAPI;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoCategory;
import com.google.api.services.youtube.model.VideoCategoryListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
//Referenced https://developers.google.com/youtube/v3/docs
//Referenced stack overflow question and modified it
public class DBConnect {
	private static final String apiKey= "AIzaSyAjylRAyzAmIm75bqwGxMe5Nj32CoyNny0"; // you can get it from https://console.cloud.google.com/apis/credentials
	private final YouTube youtube;
	public DBConnect() {
		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("APP_ID").build();
	}

	//grabs statistics on video
    public String getyoutubeitemfull_details(String videoID) throws SQLException, IOException{
        try { 
            YouTube.Videos.List listVideosRequest = youtube.videos().list("statistics");
            listVideosRequest.setId(videoID); // add list of video IDs here
            listVideosRequest.setKey(apiKey);
            VideoListResponse listResponse = listVideosRequest.execute();
            Video video = listResponse.getItems().get(0);

            BigInteger viewCount = video.getStatistics().getViewCount();
            BigInteger Likes = video.getStatistics().getLikeCount();
            BigInteger DisLikes = video.getStatistics().getDislikeCount();
            BigInteger Comments = video.getStatistics().getCommentCount();
            System.out.println("[View Count] " + viewCount);
            System.out.println("[Likes] " + Likes);
            System.out.println("[Dislikes] " + DisLikes);
            System.out.println("[Comments] " + Comments);

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    //returns helper class with video title, publishDate, duration, author, and content type id
    public VideoDetails getYouTubeVideoDetails(String videoID) throws SQLException, IOException{
        try { 
            YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet");
            listVideosRequest.setId(videoID); // add list of video IDs here
            listVideosRequest.setKey(apiKey);
            VideoListResponse listResponse = listVideosRequest.execute();
            Video video = listResponse.getItems().get(0);
            String videoTitle=video.getSnippet().getTitle();
            DateTime videoPublished =video.getSnippet().getPublishedAt();//ISL 8601
            String channelId = video.getSnippet().getChannelId();
            String categoryId = video.getSnippet().getCategoryId();
            String videoDuration =this.getYouTubeVideoDuration(videoID);
           
            String categoryName = this.getCategoryName(categoryId);
            //System.out.println("videoID: "+videoID);
            //System.out.println("Title: "+videoTitle);
            //System.out.println("Published Date: "+videoPublished);
            //System.out.println("Author ID: "+channelId);
            System.out.println("Duration: "+videoDuration);
            System.out.println("Category ID: "+categoryId);
            System.out.println("Category Name: "+categoryName);
            return new VideoDetails(videoID,videoTitle, videoPublished,channelId,categoryId,videoDuration,categoryName);
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    public String getYouTubeVideoDuration(String videoID) throws SQLException, IOException{
    	 try{YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails");
    	 listVideosRequest.setId(videoID); // add list of video IDs here
         listVideosRequest.setKey(apiKey);
         VideoListResponse listResponse = listVideosRequest.execute();
         Video video2 = listResponse.getItems().get(0);
         String videoDuration = video2.getContentDetails().getDuration();//ISO 8601
         return videoDuration;
    	 }catch (GoogleJsonResponseException e) {
             System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                 + e.getDetails().getMessage());
         } catch (IOException e) {
             System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
         } catch (Throwable t) {
             t.printStackTrace();
         }
         return null;
    }
    
    public String getCategoryName(String CategoryID) {
    	try {
    		YouTube.VideoCategories.List listCategoriesRequest=youtube.videoCategories().list(("snippet"));
    		listCategoriesRequest.setId(CategoryID);
    		listCategoriesRequest.setKey(apiKey);
    		VideoCategoryListResponse listResponse = listCategoriesRequest.execute();
    		VideoCategory category = listResponse.getItems().get(0);
    		String categoryName = category.getSnippet().getTitle();
    		return categoryName;
    	}
    	catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }
    
    
    //return helper class
    public class VideoDetails{
    	private String id;
    	private String title;
    	private DateTime publishedDate;
    	private String author;
    	private String contentType;
    	private String duration;
    	private String convertedDuration;
    	private String contentName;
    	
    	public VideoDetails(String id, String title, DateTime publishedDate, String author, String contentType, String duration, String contentName ) {
    		this.id=id;
    		this.title=title;
    		this.duration=duration;
    		this.publishedDate=publishedDate;
    		this.author=author;
    		this.contentType=contentType;
    		this.convertedDuration=TimeConverter.converttoHHMMSS(this.duration);
    		this.contentName=contentName;
    	}
    	
    	public String getId() {
    		return this.id;
    	}
    	public String getTitle() {
    		return this.title;
    	}
    	public DateTime getpublishedDate() {
    		return this.publishedDate;
    	}
    	public String getAuthor() {
    		return this.author;
    	}
    	public String getContentType() {
    		return this.contentType;
    	}
    	public String getContentName() {
    		return this.contentName;
    	}
    	public String getDuration() {
    		return this.convertedDuration;
    	}
    }
   
}