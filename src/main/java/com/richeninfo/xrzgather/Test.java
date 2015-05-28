package com.richeninfo.xrzgather;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 
 * @author mz
 * @date 2013-8-14 15:16:00
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
//        int i = 1;
//        while(true){
//            System.out.println("------第" + (i++) + "次访问-----");
//            TestThread t = new TestThread();
//            t.start();
//            Thread.sleep(1000);
//        }
//        Document doc = Jsoup.parse(html);
//        Elements es = doc.select("table[width=95%]");
//        System.out.println("----size:" + es.size());
//        HttpClient client = new DefaultHttpClient();
//        URL u = new URL(url);
//        HttpHost host = new HttpHost(u.getHost(), 80);
//        HttpRequestBase method = new HttpGet(url);
//        HttpResponse response = client.execute(host, method);
//        System.out.println(EntityUtils.toString(response.getEntity()));
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        headers.put("Host", "www.sootoo.com");
//        headers.put("Cache-Control", "max-age=0");
//        headers.put("Proxy-Connection", "keep-alive");
//        headers.put("Accept-Encoding", "gzip,deflate,sdch");
//        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
//        headers.put("Cookie", "CNZZDATA30041218=cnzz_eid%3D1244708668-1376789320-http%253A%252F%252Fwww.sootoo.com%26ntime%3D1376789320%26cnzz_a%3D21%26retime%3D1376790233130%26sin%3D%26ltime%3D1376790233130%26rtime%3D0");
//        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36");
//        HttpOperation operation = new DefaultHttpOperation();
//        String html = operation.get("http://www.sootoo.com/content/news/?page=1");
//        System.out.println(html);
        String html = FileUtils.readFileToString(new File("D:/1.txt"));
        Document doc = Jsoup.parse(new URL("http://www.gov.cn/zwgk/2013-06/20/content_2429394.htm"), 300 * 1000);
        Elements find = doc.select("#Zoom");
        System.out.println(find.get(0).outerHtml());
    }
}
