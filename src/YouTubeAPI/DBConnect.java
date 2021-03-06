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
import java.sql.SQLException;

//Referenced https://developers.google.com/youtube/v3/docs
//Referenced stack overflow question and modified it
public class DBConnect {
	private static String apiKey= "AIzaSyAjylRAyzAmIm75bqwGxMe5Nj32CoyNny0"; // you can get it from https://console.cloud.google.com/apis/credentials
	private YouTube youtube;
	public DBConnect() {
		System.out.println("startConnecting");
		
		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("APP_ID").build();
//		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
//			public void initialize(HttpRequest request) throws IOException {
//            	
//            }
//        }).setApplicationName("youtube-search").build();
		System.out.println("endConnecting");
	}

	//grabs statistics on video
//    public String getyoutubeitemfull_details(String videoID) {
//        try { 
//            YouTube.Videos.List listVideosRequest = youtube.videos().list("statistics");
//            listVideosRequest.setId(videoID); // add list of video IDs here
//            listVideosRequest.setKey(apiKey);
//            VideoListResponse listResponse = listVideosRequest.execute();
//            Video video = listResponse.getItems().get(0);
//
//            BigInteger viewCount = video.getStatistics().getViewCount();
//            BigInteger Likes = video.getStatistics().getLikeCount();
//            BigInteger DisLikes = video.getStatistics().getDislikeCount();
//            BigInteger Comments = video.getStatistics().getCommentCount();
//            System.out.println("[View Count] " + viewCount);
//            System.out.println("[Likes] " + Likes);
//            System.out.println("[Dislikes] " + DisLikes);
//            System.out.println("[Comments] " + Comments);
//
//        } catch (GoogleJsonResponseException e) {
//            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
//                + e.getDetails().getMessage());
//        } catch (IOException e) {
//            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//        return null;
//    }
    //returns helper class with video title, publishDate, duration, author, and content type id
    public VideoDetails getYouTubeVideoDetails(String videoID) throws SQLException, IOException{
        try { 
        	//System.out.println("1");
            YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet");
            listVideosRequest.setId(videoID); // add list of video IDs here
            listVideosRequest.setKey(apiKey);
            VideoListResponse listResponse = listVideosRequest.execute();
            Video video = listResponse.getItems().get(0);
            String videoTitle = video.getSnippet().getTitle();
            DateTime videoPublished = video.getSnippet().getPublishedAt();//ISL 8601
            String channelId = video.getSnippet().getChannelId();
            String categoryId = video.getSnippet().getCategoryId();
            //System.out.println("here");
            String videoDuration = this.getYouTubeVideoDuration(videoID);
           
            String categoryName = this.getCategoryName(categoryId);
//            System.out.println("videoID: "+videoID);
//            System.out.println("Title: "+videoTitle);
//            System.out.println("Published Date: "+videoPublished);
//            System.out.println("Author ID: "+channelId);
//            System.out.println("Duration: "+videoDuration);
//            System.out.println("Category ID: "+categoryId);
//            System.out.println("Category Name: "+categoryName);
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
    public String getYouTubeVideoDuration(String videoID) throws IOException{
    	 try{YouTube.Videos.List listVideosRequest = youtube.videos().list("contentDetails");
             listVideosRequest.setId(videoID); // add list of video IDs here
             listVideosRequest.setKey(apiKey);
             VideoListResponse listResponse = listVideosRequest.execute();
             Video video2 = listResponse.getItems().get(0);
             return video2.getContentDetails().getDuration();
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
            return category.getSnippet().getTitle();
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
    	private final String id;
    	private final String title;
    	private final DateTime publishedDate;
    	private final String author;
    	private final String contentType;
    	private final String duration;
    	private final String convertedDuration;
    	private final String contentName;
    	
    	public VideoDetails(String id, String title, DateTime publishedDate, String author, String contentType, String duration, String contentName ) {
    		this.id=id;
    		this.title=title;
    		this.duration=duration;
    		this.publishedDate=publishedDate;
    		this.author=author;
    		this.contentType=contentType;
    		this.convertedDuration= RegexConverter.convertTimeToHHMMSS(this.duration);
    		this.contentName=contentName;
    	}
    	
    	public String getId() {
    		return this.id;
    	}
    	public String getTitle() {
    		return this.title;
    	}
    	public DateTime getPublishedDate() {
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