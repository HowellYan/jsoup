package cn.com.howell.GetHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Ed004 {
    public static void main(String[] args){
        try
        {
            Document document = Jsoup.connect("https://weixin.sogou.com/").get();
            System.out.println(document.html());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
