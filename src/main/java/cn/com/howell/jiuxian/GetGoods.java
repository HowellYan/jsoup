package cn.com.howell.jiuxian;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        for (int i=1;i<100000;i++){
            getGoodsJson(i);
        }

    }

    public static void getGoodsJson(int i){
        try
        {
            Document document = Jsoup.connect("http://www.jiuxian.com/goods-"+i+".html").get();
            //System.out.println(document.toString());
            if(!document.title().contains("页面出错啦")) {
                ReptileGoodsInfo info = new ReptileGoodsInfo();
                info.setId(i);
                info.setPrice(getPrice(i));

                Matcher matcher = save.matcher(document.html());
                while (matcher.find()){
                    long collection = Long.parseLong(matcher.group().replaceAll("<span>收藏 （<em>","").replaceAll("</em>） </span></a>",""));
                   //收藏
                    info.setCollection(collection);
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
                        if(info.getLbarr() == null){
                            info.setLbarr(url); // 轮播图片
                        }else{
                            info.setLbarr(info.getLbarr()+";"+url); // 轮播图片
                        }
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
                        String image_link = jsonObject.getString("image_link");
                        String simage_link = jsonObject.getString("simage_link");
                        String name = jsonObject.getString("name");
                        String brand = jsonObject.getString("brand");
                        String winery = jsonObject.getString("winery");
                        String sn = jsonObject.getString("sn");
                        String mtl = jsonObject.getString("mtl");
                        String net_content = jsonObject.getString("net_content");
                        String carton_size = jsonObject.getString("carton_size");
                        JSONArray category = jsonObject.getJSONArray("category").getJSONArray(0);
                        getGoodsImg(image_link,i+"_image_link_.jpg","d:\\image\\");
                        getGoodsImg(image_link,i+"_simage_link_.jpg","d:\\image\\");

                        info.setGoodsName(name);
                        info.setGoodsType(category.getString(0));
                        info.setGoodsBrand(brand);
                        info.setWinery(winery);
                        info.setGoodsSn(sn);
                        info.setScent(mtl);
                        info.setCartonSize(carton_size);
                        info.setNetContent(net_content);
                        info.setCover(image_link+";"+simage_link);
                    }
                }
                insert(info);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取产品价格
     * @param i
     * @throws IOException
     */
    public static double getPrice(int i) throws IOException {
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
            JSONObject jsonObject = new JSONObject(respContent);
            if(!jsonObject.isNull("act") && !jsonObject.getJSONObject("act").isNull("markPrice")){
                return jsonObject.getJSONObject("act").getDouble("markPrice");
            }
        }
        return 0.0;
    }


    /**
     * 保存商品图片
     * @param urlString
     * @param filename
     * @param savePath
     * @throws Exception
     */
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

    public static Connection getConnection(){
        try {
            return  DriverManager.getConnection("jdbc:mysql://192.168.59.4:3306/jsoup","root","Aa123456@zx");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insert(ReptileGoodsInfo info){
        Connection connection = getConnection();
        PreparedStatement preparedStatement  = null;
        try {
            preparedStatement =  connection.prepareStatement("insert into reptile_goods_info(id,goods_sn,goods_name,sub_title,goods_type,goods_brand,place_origin,price,cover,ingesting_time,bottle_stopper,scent,grape_variety,collocation_food,storage_conditions,taste,net_content,color,carton_size,introduce,winery,description,materials,process,collection,lbarr)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setLong(1, info.getId());
            preparedStatement.setString(2, info.getGoodsSn());
            preparedStatement.setString(3, info.getGoodsName());
            preparedStatement.setString(4, info.getSubTitle());
            preparedStatement.setString(5, info.getGoodsType());
            preparedStatement.setString(6, info.getGoodsBrand());
            preparedStatement.setString(7, info.getPlaceOrigin());
            preparedStatement.setDouble(8, info.getPrice());
            preparedStatement.setString(9, info.getCover());
            preparedStatement.setString(10, info.getIngestingTime());
            preparedStatement.setString(11, info.getBottleStopper());
            preparedStatement.setString(12, info.getScent());
            preparedStatement.setString(13, info.getGrapeVariety());
            preparedStatement.setString(14, info.getCollocationFood());
            preparedStatement.setString(15, info.getStorageConditions());
            preparedStatement.setString(16, info.getTaste());
            preparedStatement.setString(17, info.getNetContent());
            preparedStatement.setString(18, info.getColor());
            preparedStatement.setString(19, info.getCartonSize());
            preparedStatement.setString(20, info.getIntroduce());
            preparedStatement.setString(21, info.getWinery());
            preparedStatement.setString(22, info.getDescription());
            preparedStatement.setString(23, info.getMaterials());
            preparedStatement.setString(24, info.getProcess());
            preparedStatement.setLong(25, info.getCollection());
            preparedStatement.setString(26, info.getLbarr());
            int i= preparedStatement.executeUpdate();
            System.out.println(i);
            if(i>=1) {
                System.out.println("新增：成功");
            }else {
                System.out.println("新增：失败");
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
