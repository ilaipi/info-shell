package com.richeninfo.xrzgather.kernel.resulthandler;

import com.richeninfo.xrzgather.kernel.Info;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;

/**
 * 此处理器是插件性质的。在采集列表标签结束后执行。
 * 执行此处理器，必须先调用isAttachment方法，如果结果为true，才能调用handle方法
 * @author mz
 * @date 2013-8-25 9:29:45
 */
public class AttachmentFromListTag extends AttachmentFromInfoTextHandler{
    private String infoUrl = "";
    private String suffix = "";
    @Override
    public void handle(Map<String, Object> currResult) {
        //需要下载附件
        String fileName = (String) currResult.get(Info.INFO_TITLE.name()) + "." + suffix;
        String groupCode = (String) currResult.get(Info.INFO_UUID.name());
        String uuid = UUID.randomUUID().toString();
        saveAttachment(infoUrl, fileName, groupCode, uuid);
    }
    
    public boolean isAttachment(Map<String, String> fieldValueMap){
        infoUrl = fieldValueMap.get(Info.INFO_URL.name());
        if(!infoUrl.contains(".")){
            return false;
        }
        suffix = StringUtils.substringAfterLast(infoUrl, ".");
        if(getFileSuffix().contains("," + suffix + ",")){
            return true;
        } else{
            return false;
        }
    }
}
