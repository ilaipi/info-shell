package com.richeninfo.xrzgather.kernel.page;

import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.locate.Locate;
import java.util.List;

/**
 *
 * @author mz
 * @date 2013-8-8 10:24:31
 */
public class Page {
    protected String pageHtml;
    protected String pageUrl;
    protected List<Field> fields;
    
    protected Boolean multiPage;
    protected Integer currPageNum;
    protected String cssSelector;
    protected Integer firstPageNum;
    
    protected Locate locate;

    public Locate getLocate() {
        return locate;
    }

    public void setLocate(Locate locate) {
        this.locate = locate;
    }

    public String getPageHtml() {
        return pageHtml;
    }

    public void setPageHtml(String pageHtml) {
        this.pageHtml = pageHtml;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Boolean getMultiPage() {
        if(multiPage == null){
            multiPage = Boolean.FALSE;
        }
        return multiPage;
    }

    public void setMultiPage(Boolean multiPage) {
        this.multiPage = multiPage;
    }

    public Integer getCurrPageNum() {
        if(currPageNum == null){
            currPageNum = getFirstPageNum();
        }
        return currPageNum;
    }

    public void setCurrPageNum(Integer currPageNum) {
        this.currPageNum = currPageNum;
    }

    public String getCssSelector() {
        return cssSelector;
    }

    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    public Integer getFirstPageNum() {
        return firstPageNum;
    }

    public void setFirstPageNum(Integer firstPageNum) {
        this.firstPageNum = firstPageNum;
    }
}
