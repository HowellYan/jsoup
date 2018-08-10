package cn.com.howell.GetHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Ed002 {
    public static void main(String[] args){
        try
        {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            System.out.println(document.title());
            System.out.println(document.getElementById("su").val());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
