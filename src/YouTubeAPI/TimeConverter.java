package YouTubeAPI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vikash on 01-04-2017.
 */
public class TimeConverter {

    public static String converttoHHMMSS(String youtubetime) {

        String pattern = new String("^PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+))S?$");
        String pattern2 = new String("^PT(?:(\\d+)M)?(?:(\\d+))S?$");
        String pattern3 = new String("^PT(?:(\\d+))S?$");
        String pattern4 = new String("^PT(?:(\\d+))M?$");
        Pattern r = Pattern.compile(pattern);
        Pattern r2= Pattern.compile(pattern2);
        Pattern r3=Pattern.compile(pattern3);
        Pattern r4=Pattern.compile(pattern4);
        
        String result=null;

        Matcher m = r.matcher(youtubetime);
        Matcher m2 = r2.matcher(youtubetime);
        Matcher m3 = r3.matcher(youtubetime);
        Matcher m4=r4.matcher(youtubetime);
        if (m.find()) {
        	System.out.println("pattern1");
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

            if (mm != null) {
                result = "00:"+mm + ":" + ss;
            }
        }
        else if (m3.find()) {
        	String ss = m3.group(1);
            ss = ss !=null?ss:"0";
            
            if (ss != null) {
                result = "00:00:"+ss;
            }
        }
        else if (m4.find()) {
        	String mm = m4.group(1);
            mm = mm !=null?mm:"0";
            
            if (mm != null) {
                result = "00:"+mm+":00";
            }
        }
        else {
            result = "00:00:00";
        }
        return result;
    }
}
