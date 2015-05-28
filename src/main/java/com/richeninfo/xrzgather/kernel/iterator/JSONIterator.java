package com.richeninfo.xrzgather.kernel.iterator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mz
 * @date 2013-8-21 18:57:17
 */
public class JSONIterator extends InfoIterator{

    @Override
    public void parse(String html, String listCssSelector) {
        if("array".equalsIgnoreCase(listCssSelector)){
            list = JSON.parseArray(html);
        } else {
            //object[arrayKey]
            Pattern pattern = Pattern.compile("object\\[([^\\]]*)\\]", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(listCssSelector);
            if(matcher.find()){
                String arrayKey = matcher.group(1);
                if(StringUtils.isBlank(arrayKey)){
                    throw new IllegalArgumentException("选择器中没有指定arrayKey。选择器：" + listCssSelector);
                }
                JSONObject obj = JSON.parseObject(html);
                if(obj == null){
                    throw new IllegalArgumentException("无效的json数据");
                }
                list = obj.getJSONArray(arrayKey);
            } else{
                throw new IllegalArgumentException("无效的列表标签选择器：" + listCssSelector);
            }
        }
    }

    @Override
    public String getNextString() {
        String json = ((JSONObject)list.get(index)).toJSONString();
        return json;
    }

    /**
     * if is json data, do not need unnecessary data for layout,so just return true
     * @param linkCssSelector   链接的选择器
     * @return 
     */
    @Override
    public boolean hasSelectLink(String linkCssSelector) {
//        JSONObject obj = (JSONObject) list.get(index + 1);
//        if(obj.containsKey(linkCssSelector)){
//            return true;
//        }
//        return false;
        return true;
    }
    
}
