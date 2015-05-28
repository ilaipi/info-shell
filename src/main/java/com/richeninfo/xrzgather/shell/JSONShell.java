package com.richeninfo.xrzgather.shell;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.http.utils.HttpOperationUtils;
import com.richeninfo.xrzgather.kernel.Info;
import com.richeninfo.xrzgather.kernel.exceptions.ContinueException;
import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.field.JSONField;
import com.richeninfo.xrzgather.kernel.iterator.JSONIterator;
import com.richeninfo.xrzgather.kernel.page.ListPage;

/**
 * 
 * @author mz
 * @date 2013-8-21 10:50:30
 */
public class JSONShell extends XMLLinkShell{
    public JSONShell(){
        this.setIterator(new JSONIterator());
    }
    @Override
    protected void gatherListTag(String tagHtml, ListPage listPage) {
        try{
            JSONObject json = JSON.parseObject(tagHtml);
            if(json != null){
                String title = json.getString(getTitleValue());
                String link = json.getString(getLinkValue());
                fieldValueMap.put(Info.INFO_TITLE.name(), title);
                fieldValueMap.put(Info.ORIGINAL_INFO_TITLE.name(), title);
                fieldValueMap.put(Info.INFO_URL.name(), HttpOperationUtils.getAbsoluteUrl(realListPageUrl, link));
            }
        } catch(Exception e){
            logger.warn("json转换异常", e);
        }
    }
    
    @Override
    protected void gatherListField(String tagHtml, Field listField) throws ContinueException{
        try{
            JSONObject json = JSON.parseObject(tagHtml);
            if(json != null){
                String value = json.getString(((JSONField)listField).getJsonKey());
                fieldValueMap.put(listField.getName(), value);
            }
        } catch(Exception e){
            logger.warn("json转换异常", e);
            throw new ContinueException("字段{" + listField.getName() + "}采集异常。");
        }
    }
}
