package cn.com.howell.GetHtml.jd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ed001 {
    private static Pattern shouji = Pattern.compile("([^<]*)手机");
    public static void main(String[] args){
        try
        {
            Document document = Jsoup.connect("https://www.jd.com/").get();
            //System.out.println(document.html());
            Matcher matcher = shouji.matcher(document.html());
            if(matcher.find()) {
                System.out.println(matcher.group());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
