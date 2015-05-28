package com.richeninfo.xrzgather;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author mz
 * @date 2013-8-16 14:30:33
 */
public class TestThread extends Thread{

    @Override
    public void run() {
        String url = "http://translate.google.cn/";
        HttpOperation operation = new DefaultHttpOperation();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        
        Map<String, String> nameValues = new HashMap<>();
        nameValues.put("sl", "en");
        nameValues.put("js", "n");
        nameValues.put("prev", "_t");
        nameValues.put("tl", "zh-CN");
        nameValues.put("hl", "zh-CN");
        nameValues.put("ie", "UTF-8");
        nameValues.put("text", "$20 per 1 M characters of text, where the charges are adjusted in proportion to the number of characters actually provided.$20 per 1 M characters of text, where the charges are adjusted in proportion to the number of characters actually provided.$20 per 1 M characters of text, where the charges are adjusted in proportion to the number of characters actually provided.$20 per 1 M characters of text, where the charges are adjusted in proportion to the number of characters actually provided.");
        String html = operation.post(url, headers, nameValues);
        if(!html.contains("其中电荷被调整在实际提供的字符的数目成比例")){
                throw new RuntimeException("翻译不成功！");
        }
        try {
            FileUtils.writeStringToFile(new File("D:/htmls/" + this + ".html"), html);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
