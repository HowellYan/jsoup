package cn.com.howell.GetHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Ed003 {
    public static void main(String[] args){
        try
        {
            while (true) {

            String randomStr = randomStr(22);
            Document document = Jsoup.connect("https://mp.weixin.qq.com/s/" + randomStr).get();
                if(!document.text().contains("参数错误 微信公众平台运营中心")) {
                    System.out.println("--------------start");
                    System.out.println(document.baseUri());
                    System.out.println(document.select("meta[property=og:title]").attr("content"));
                    System.out.println(document.text());
                    System.out.println("--------------end");
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 得到随机字符
     * @param n
     * @return
     */
    public static String randomStr(int n) {
        String str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";
        String str2 = "";
        int len = str1.length();
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str2 = str2 + str1.charAt((int) r);
        }
        return str2;
    }
}
