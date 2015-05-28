package com.richeninfo.xrzgather;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import jodd.jerry.Jerry;
import org.junit.Test;

/**
 * 
 * @author mz
 * @date 2013-8-22 8:52:59
 */
public class TestCssSelector {
    @Test
    public void testEvenSelector(){
        String url = "http://www.aqsiq.gov.cn/zjxw/zjxw/xwfbt/index.htm";
        HttpOperation operation = new DefaultHttpOperation();
        String html = operation.get(url, null, null);
//        Document doc = Jsoup.parse(html);
//        Elements find = doc.select("table[width=95%] tr:even");
//        System.out.println(find.size());
        
        Jerry j = Jerry.$("table[width=95%] tr", Jerry.jerry(html));
        System.out.println(j.size());
    }
}
