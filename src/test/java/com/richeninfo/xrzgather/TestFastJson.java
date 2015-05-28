package com.richeninfo.xrzgather;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import com.richeninfo.xrzgather.kernel.iterator.InfoIterator;
import com.richeninfo.xrzgather.kernel.iterator.JSONIterator;
import com.richeninfo.xrzgather.kernel.iterator.JsoupIterator;
import com.richeninfo.xrzgather.utils.FlowConfigUtil;
import org.junit.Test;

import java.util.Map;

/**
 * 
 * @author mz
 * @date 2013-8-21 10:16:00
 */
public class TestFastJson {

    @Test
    public void testJSONIterator(){
        HttpOperation operation = new DefaultHttpOperation();
        String requestHeaderStr = "Content-Type: application/x-www-form-urlencoded\n" +
"Host: search.miit.gov.cn\n" +
"Origin: http://search.miit.gov.cn\n" +
"Referer: http://search.miit.gov.cn/search/search_gxb.jsp\n" +
"User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.71 Safari/537.36\n" +
"X-Requested-With: XMLHttpRequest";
        String requestBodyStr = "url=http://www.miit.gov.cn/n11293472/n11293832/n12845605/&fullText=http&sortKey=showTime&sortFlag=-1&sortType=1&num=20&year=年 份&pageSize=10";
        Map<String, String> headers = FlowConfigUtil.buildMap(requestHeaderStr, "\n", ":");
        Map<String, String> bodys = FlowConfigUtil.buildMap(requestBodyStr, "&", "=");
        String html = operation.get("http://search.miit.gov.cn/search/search?pageNow=2", headers, bodys);
        
        String listCssSelector = "object[array]";
        InfoIterator iterator = new JSONIterator().build(html, listCssSelector);
        System.out.println(iterator.size());
        int total = 1;
        while(iterator.hasNext()){
            total++;
            System.out.println("----index:" + iterator.getIndex());
            iterator.goNext();
        }
        System.out.println("total:" + total + "////size:" + iterator.size());
        assert total == iterator.size();
    }
    
    @Test
    public void testJsoupIterator(){
        HttpOperation operation = new DefaultHttpOperation();
        String url = "http://www.sootoo.com/content/news/?page=1";
        String html = operation.get(url, null, null);
        InfoIterator iterator = new JsoupIterator().build(html, "div#t11_m_left p");
        System.out.println(iterator.size());
        int total = 1;
        while(iterator.hasNext()){
            total++;
            iterator.goNext();
        }
        System.out.println("total:" + total + "////size:" + iterator.size());
        assert total == iterator.size();
    }
    
    @Test
    public void testXWFBT(){
        HttpOperation operation = new DefaultHttpOperation();
        String url = "http://www.aqsiq.gov.cn/zjxw/zjxw/xwfbt/index.htm";
        String html = operation.get(url, null, null);
        InfoIterator iterator = new JsoupIterator().build(html, "table[width=95%] tr");
        System.out.println(iterator.size());
        int total = 1;
        while(iterator.hasNext()){
            total++;
            iterator.goNext();
        }
        System.out.println("total:" + total + "////size:" + iterator.size());
        assert total == iterator.size();
    }
}
