package cn.com.howell.GetHtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * @author howell
 * @date 25/2/2021 8:20 下午
 */
public class Ed005 {
    public static void main(String[] args){
        List<Map<String, String>>  mapList = getCodeJson(new ArrayList<Map<String, String>>(),"https://xingzhengquhua.bmcx.com", "", 10);
        // todo 保存
        System.out.println(mapList.size());
    }


    public static List<Map<String, String>> getCodeJson(List<Map<String, String>> list, String url, String parentCode, int type) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements table = document.getElementsByTag("table");
            Elements trList = table.get(1).getElementsByTag("tr");
            for (int i=0;i < trList.size();i++) {
                Elements aList = trList.get(i).getElementsByTag("a");
                if(aList.size() >= 2) {
                    Map<String, String> stringStringMap = new HashMap<String, String>(4);
                    stringStringMap.put("parentCode", parentCode);
                    stringStringMap.put("type", "" + type);
                    String href = "";
                    if (aList.get(0) != null) {
                        href = aList.get(0).attr("href");
                        stringStringMap.put("name", aList.get(0).html());
                    }
                    if (aList.get(1) != null) {
                        parentCode = aList.get(1).html();
                        stringStringMap.put("code", aList.get(1).html());
                    }
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getCodeJson(list, "https://xingzhengquhua.bmcx.com" + href, parentCode, type + 10);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
