package cn.com.howell.jiuxian;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetGoods {
    private static Pattern lbarr = Pattern.compile("lbarr\\[\\d]+=\\[\'http://([a-zA-Z0-9.\\/])+']");
    private static Pattern BFD_INFO = Pattern.compile("_BFD.BFD_INFO = \\{");
    private static Pattern id = Pattern.compile("\"id\" : \"[\\u0000-\\uFFFF]+\"");
    private static Pattern save = Pattern.compile("<span>收藏 （<em>[\\d]+</em>） </span></a>");

    public static void main(String[] args){
        //for (int i=1;i<10000;i++){
            getGoodsJson(2);
        //}

    }

    public static void getGoodsJson(int i){
        try
        {
            Document document = Jsoup.connect("http://www.jiuxian.com/goods-"+i+".html").get();
            //System.out.println(document.toString());
            if(!document.title().contains("页面出错啦")) {
                getPrice(i);
                Matcher matcher = save.matcher(document.html());
                while (matcher.find()){
                    System.out.println(matcher.group());
                }

                Elements scripts = document.select("script");
                for (Element script : scripts) {
                    String str = script.html();
                    //System.out.println(str);
                    matcher = lbarr.matcher(str);
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

    public static void getPrice(int i) throws IOException {
        String url = "http://www.jiuxian.com/pro/selectProActByProId.htm";
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        String respContent = null;
//      json方式
//        JSONObject jsonParam = new JSONObject();
//        jsonParam.put("proId", i);
//        jsonParam.put("resId", 2);
//        StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
//        entity.setContentEncoding("UTF-8");
//        entity.setContentType("application/json");
//        httpPost.setEntity(entity);

//       表单方式
       List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
       pairList.add(new BasicNameValuePair("proId", String.valueOf(i)));
       pairList.add(new BasicNameValuePair("resId", "2"));
       httpPost.setEntity(new UrlEncodedFormEntity(pairList, "utf-8"));
       httpPost.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");


        HttpResponse resp = client.execute(httpPost);
        if(resp.getStatusLine().getStatusCode() == 200) {
            HttpEntity he = resp.getEntity();
            respContent = EntityUtils.toString(he,"UTF-8");
            System.out.println(respContent);
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
        // TODO: 上传到文件服务器
    }


    public static String getVal(String str,Pattern p){
        Matcher m = p.matcher(str);
        if (m.find()){
            return m.group().trim();
        }
        return null;
    }

}
