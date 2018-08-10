package cn.com.howell.jiuxian;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetGoods {
    private static Pattern lbarr = Pattern.compile("lbarr\\[\\d+]=\\[\'http://([a-zA-Z0-9.\\/])+']");
    private static Pattern BFD_INFO = Pattern.compile("_BFD.BFD_INFO = \\{");
    private static Pattern id = Pattern.compile("\"id\" : \"[\\u0000-\\uFFFF]+\"");

    public static void main(String[] args){
        try
        {
            Document document = Jsoup.connect("http://www.jiuxian.com/goods-1991.html").get();
            Elements scripts = document.select("script");
            for(Element script : scripts) {

                String str = script.html();
                //System.out.println(str);
                Matcher matcher = lbarr.matcher(str);
                while(matcher.find()) {
                    System.out.println(matcher.group());
                }
                matcher = BFD_INFO.matcher(str);
                if(matcher.find()){
                    System.out.println(getVal(str, id));
                }


            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static String getVal(String str,Pattern p){
        Matcher m = p.matcher(str);
        if (m.find()){
            return m.group().trim();
        }
        return null;
    }

}
