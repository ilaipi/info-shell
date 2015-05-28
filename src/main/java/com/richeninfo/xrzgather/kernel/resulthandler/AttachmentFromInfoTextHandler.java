package com.richeninfo.xrzgather.kernel.resulthandler;

import com.richeninfo.http.utils.HttpOperationUtils;
import com.richeninfo.xrzgather.kernel.Info;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import jodd.jerry.Jerry;
import jodd.lagarto.dom.Node;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * 从资讯的正文中找到附件的链接进行下载。下载后存2进制流到附件表，生成的uuid替换到原链接的href属性。
 * @see #attachSelector 默认值为 a，即正文中所有的a标签
 * @author mz
 * @date 2013-8-20 9:59:50
 */
public class AttachmentFromInfoTextHandler extends SqlExecuteHandler{
    /**
     * 附件标签选择器
     */
    private String attachSelector;
    /**
     * <pre>
     * 文件类型。如W020130806620700307575.pdf，则为pdf。可以支持多个，以","链接。
     * 默认值：pdf,doc,xls,docx,xlsx
     * </pre>
     */
    private String fileSuffix;
    
    /**
     * 需要替换的字符串，必须是正则表达式
     */
    private String toReplace;
    /**
     * 要替换为的字符串
     */
    private String replaceWith;
    
    public static final String DEFAULT_FILE_SUFFIX = ",pdf,doc,xls,docx,xlsx,";

    @Override
    public void handle(Map<String, Object> currResult) {
        String infoText = (String) currResult.get(Info.INFO_TEXT.name());
        if(StringUtils.isBlank(infoText)){
            return;
        }
        attachSelector = getAttachSelector();
        Jerry j = Jerry.$(attachSelector, Jerry.jerry(infoText));
        List<String> gatherAttachment = new ArrayList<>();
        if(j.size() > 0){
            for(Iterator<Jerry> it = j.iterator();it.hasNext();){
                Jerry jerry = it.next();
                Node node = jerry.get(0);
                if(node == null){
                    continue;
                }
                String nodeName = node.getNodeName();
                String attr = "";
                if("a".equalsIgnoreCase(nodeName)){
                    attr = "href";
                } else if("img".equalsIgnoreCase(nodeName)){
                    attr = "src";
                }
                String href = jerry.attr(attr);
                if(StringUtils.isBlank(href)){//发改委居然有a标签不写href属性的！
                    continue;
                }
                if(href.lastIndexOf(".") == -1){//如果没有点，直接过滤掉
                    continue;
                }
                String suffix = StringUtils.substringAfterLast(href, ".");
                if(!StringUtils.containsIgnoreCase(getFileSuffix(), "," + suffix + ",")){//如果不以指定的后缀结尾，不采集
                    continue;
                }
                if(gatherAttachment.contains(href)){
                    continue;
                }
                gatherAttachment.add(href);
                href = HttpOperationUtils.getAbsoluteUrl((String) currResult.get(Info.INFO_URL.name()), href);
                String fileName = jerry.text();
                if(!fileName.endsWith("."+suffix)){
                    fileName = fileName + "." + suffix;
                }
                String groupCode = (String) currResult.get(Info.INFO_UUID.name());
                String uuid = UUID.randomUUID().toString();
                boolean downloadAttch = saveAttachment(href, fileName, groupCode, uuid);
                if(downloadAttch){
                    jerry.attr(attr, "XRZ.getAttachment(" + uuid + ")");
                }
            }
        }
        currResult.remove(Info.INFO_TEXT.name());
        currResult.put(Info.INFO_TEXT.name(), j.root().html());
    }
    
    protected boolean saveAttachment(String url, String fileName, String groupCode, String uuid){
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(Info.INFO_URL.name(), url);
        attachment.put(Info.INFO_FILE_NAME.name(), fileName);
        attachment.put(Info.INFO_GROUP_CODE.name(), groupCode);
        attachment.put(Info.INFO_UUID.name(), uuid);
        boolean result = false;
        if(operation != null){
            HttpGet get = new HttpGet(url);
            try {
                HttpResponse response = operation.getClient().execute(get);
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    attachment.put(Info.INFO_FILE_BYTES.name(), EntityUtils.toByteArray(entity));
                    super.handle(attachment);
                    result = true;
                } else{
                    logger.warn("附件下载失败。资讯uuid：[" + groupCode + "]。附件标题：" + fileName);
                }
            } catch (Exception ex) {
                logger.warn("附件下载失败。资讯uuid：[" + groupCode + "]。附件标题：" + fileName, ex);
            }
        }
        return result;
    }

    public String getFileSuffix() {
        if(StringUtils.isBlank(fileSuffix)){
            fileSuffix = DEFAULT_FILE_SUFFIX;
        } else{
            fileSuffix = DEFAULT_FILE_SUFFIX + fileSuffix + ",";//可以直接用contains
        }
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getAttachSelector() {
        if(StringUtils.isBlank(attachSelector)){
            attachSelector = "a";
        }
        return attachSelector.trim();
    }

    public void setAttachSelector(String attachSelector) {
        this.attachSelector = attachSelector;
    }

    public String getToReplace() {
        if(StringUtils.isBlank(toReplace)){
            toReplace = "\\s*|&nbsp;";
        }
        return toReplace;
    }

    public void setToReplace(String toReplace) {
        this.toReplace = toReplace;
    }

    public String getReplaceWith() {
        if(StringUtils.isBlank(replaceWith)){
            replaceWith = "";
        }
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }
}
