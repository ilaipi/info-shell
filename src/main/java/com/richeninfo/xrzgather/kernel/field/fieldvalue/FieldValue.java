package com.richeninfo.xrzgather.kernel.field.fieldvalue;

/**
 *
 * @author mz
 * @date 2013-8-8 12:21:56
 */
public abstract class FieldValue {
    protected String pageUrl;
    protected String locateHtml;
    
    public abstract String getValue();

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getLocateHtml() {
        return locateHtml;
    }

    public void setLocateHtml(String locateHtml) {
        this.locateHtml = locateHtml;
    }
}
