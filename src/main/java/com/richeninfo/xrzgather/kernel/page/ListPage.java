package com.richeninfo.xrzgather.kernel.page;

import com.richeninfo.xrzgather.kernel.field.Field;
import java.util.List;

/**
 *
 * @author mz
 * @date 2013-8-8 10:27:48
 */
public class ListPage extends Page{
    protected List<Field> globalFields;
    protected String listCssSelector;
    protected String linkCssSelector;
    protected String pageHandle;

    public void setGlobalFields(List<Field> globalFields) {
        this.globalFields = globalFields;
    }

    public List<Field> getGlobalFields() {
        return globalFields;
    }

    public String getListCssSelector() {
        return listCssSelector;
    }

    public void setListCssSelector(String listCssSelector) {
        this.listCssSelector = listCssSelector;
    }

    public String getLinkCssSelector() {
        return linkCssSelector;
    }

    public void setLinkCssSelector(String linkCssSelector) {
        this.linkCssSelector = linkCssSelector;
    }

    public String getPageHandle() {
        return pageHandle;
    }

    public void setPageHandle(String pageHandle) {
        this.pageHandle = pageHandle;
    }
}
