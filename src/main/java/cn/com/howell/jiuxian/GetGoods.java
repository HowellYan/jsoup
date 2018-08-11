package cn.com.howell.jiuxian;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetGoods {
    private static Pattern lbarr = Pattern.compile("lbarr\\[\\d+]=\\[\'http://([a-zA-Z0-9.\\/])+']");
    private static Pattern BFD_INFO = Pattern.compile("_BFD.BFD_INFO = \\{");
    private static Pattern id = Pattern.compile("\"id\" : \"[\\u0000-\\uFFFF]+\"");

    public static void main(String[] args){
        //for (int i=1;i<10000;i++){
            getGoodsJson(1);
        //}

    }

    public static void getGoodsJson(int i){
        try
        {
            Document document = Jsoup.connect("http://www.jiuxian.com/goods-"+i+".html").get();
            //System.out.println(document.toString());
            if(!document.title().contains("页面出错啦")) {
                Elements scripts = document.select("script");
                for (Element script : scripts) {
                    String str = script.html();
                    //System.out.println(str);
                    Matcher matcher = lbarr.matcher(str);
                    int j = 0;
                    while (matcher.find()) {
                        String url = matcher.group()
                                .replaceAll("']","")
                                .replaceAll("lbarr\\[\\d+]=\\[\'","");
                        System.out.println(url);
                        j++;
                        getGoodsImg(url, i+"_"+j+"_.jpg","d:\\image\\");
                    }
                    matcher = BFD_INFO.matcher(str);
                    if (matcher.find()) {
                        str = getVal(str, id)
                                .replaceAll(" ", "")
                                .replaceAll("\n", " ")
                                .replaceAll("// ", "//")
                                .replaceAll(",//", ", //")
                                .replaceAll("getUserId\\(\\)", "\"\"");

                        str = str.replaceAll(" //[\\S]+", "");
                        str = "{" + str + "}";
                        //System.out.println(str);
                        JSONObject jsonObject = new JSONObject(str);
                        System.out.println(jsonObject.toString());

                    }
                }
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getGoodsImg(String urlString, String filename,String savePath) throws Exception {
            // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5*1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf=new File(savePath);
        if(!sf.exists()){
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath()+"\\"+filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();

    }


    public static String getVal(String str,Pattern p){
        Matcher m = p.matcher(str);
        if (m.find()){
            return m.group().trim();
        }
        return null;
    }

}
