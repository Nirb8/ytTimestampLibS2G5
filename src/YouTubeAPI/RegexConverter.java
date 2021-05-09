package YouTubeAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vikash on 01-04-2017.
 */
public class RegexConverter {

    public static String convertTimeToHHMMSS(String youtubeTime) {

        String pattern = new String("^PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+))S?$");
        String pattern2 = new String("^PT(?:(\\d+)M)?(?:(\\d+))S?$");
        String pattern3 = new String("^PT(?:(\\d+))S?$");
        String pattern4 = new String("^PT(?:(\\d+))M?$");
        Pattern r = Pattern.compile(pattern);
        Pattern r2= Pattern.compile(pattern2);
        Pattern r3=Pattern.compile(pattern3);
        Pattern r4=Pattern.compile(pattern4);
        
        String result;

        Matcher m = r.matcher(youtubeTime);
        Matcher m2 = r2.matcher(youtubeTime);
        Matcher m3 = r3.matcher(youtubeTime);
        Matcher m4=r4.matcher(youtubeTime);
        if (m.find()) {
            String hh = m.group(1);
            String mm = m.group(2);
            String ss = m.group(3);
            mm = mm !=null?mm:"0";
            ss = ss !=null?ss:"0";
            result = String.format("%02d:%02d", Integer.parseInt(mm), Integer.parseInt(ss));

            if (hh != null) {
                result = hh + ":" + result;
            }
            else {
            	result="00:"+result;
            }
        }
        else if (m2.find()) {
        	String mm = m2.group(1);
            String ss = m2.group(2);
            mm = mm !=null?mm:"0";
            ss = ss !=null?ss:"0";
            result = String.format("%02d",Integer.parseInt(ss));

            result = "00:"+mm + ":" + ss;
        }
        else if (m3.find()) {
        	String ss = m3.group(1);
            ss = ss !=null?ss:"0";

            result = "00:00:"+ss;
        }
        else if (m4.find()) {
        	String mm = m4.group(1);
            mm = mm !=null?mm:"0";

            result = "00:"+mm+":00";
        }
        else {
            result = "00:00:00";
        }
        System.out.println("Found time: " + result);
        return result;
    }

    public static String convertLinkToYTVideoID(String youtubeLink) {
        if (youtubeLink.isEmpty()) {
            return youtubeLink;
        }

        String pattern = new String("^((?:\\w|-|_){11})$");
        String pattern2 = new String("youtube\\.com\\/watch\\?v=((?:\\w|-|_){11})$");
        String pattern3 = new String("youtu\\.be\\/((?:\\w|-|_){11})$");
//        String pattern = "(.{11})";
//        String pattern2 = "youtube[.]com\\/watch\\?v=(.{11})";
//        String pattern3 = "youtu[.]be\\/(.{11})"; 
        
        
        Pattern r = Pattern.compile(pattern);
        Pattern r2= Pattern.compile(pattern2);
        Pattern r3=Pattern.compile(pattern3);

        String result;

        Matcher m = r.matcher(youtubeLink);
        Matcher m2 = r2.matcher(youtubeLink);
        Matcher m3 = r3.matcher(youtubeLink);
        if (m.find()) {
            result = m.group(1);
        }
        else if (m2.find()) {
            result = m2.group(1);
        }
        else if (m3.find()) {
            result = m3.group(1);
        }
        else {
            throw new IllegalArgumentException("This is not a youtube link or video ID.");
        }
        System.out.println("found ID: " + result);
        return result;
    }
}
