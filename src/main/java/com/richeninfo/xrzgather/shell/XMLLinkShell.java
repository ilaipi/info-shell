package com.richeninfo.xrzgather.shell;

import com.richeninfo.http.utils.HttpOperationUtils;
import com.richeninfo.xrzgather.kernel.Info;
import com.richeninfo.xrzgather.kernel.exceptions.StopException;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import jodd.jerry.Jerry;

/**
 * <pre>
 * 开发原型：中华人民共和国国家发展和改革委员会(http://www.ndrc.gov.cn/zcfb/default.htm)
 * 与普通壳相比，特殊的地方在于在列表标签中，解析a标签方式不同，其它完全相同。
 * </pre>
 * @author mz
 * @date 2013-8-19 16:39:51
 */
public class XMLLinkShell extends CommonShell{
    private String titleValue;
    private String linkValue;

    @Override
    protected void gatherListTag(String tagHtml, ListPage listPage) {
        Jerry j = Jerry.jerry(tagHtml);
        Jerry titles = j.$(titleValue);
        Jerry links = j.$(linkValue);
        if(titles.size() == 0){
            throw new StopException("不能从列表标签中找到标题");
        }
        if(links.size() == 0){
            throw new StopException("不能从列表标签中找到链接");
        }
        String title = titles.text();
        String link = links.text();
        fieldValueMap.put(Info.INFO_TITLE.name(), title);
        fieldValueMap.put(Info.ORIGINAL_INFO_TITLE.name(), title);
        try{
            fieldValueMap.put(Info.INFO_URL.name(), HttpOperationUtils.getAbsoluteUrl(realListPageUrl, link));
        } catch(Exception e){
            logger.warn("链接转换异常", e);
        }
    }

    public String getTitleValue() {
        return titleValue;
    }

    public void setTitleValue(String titleValue) {
        this.titleValue = titleValue;
    }

    public String getLinkValue() {
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
    }
}
