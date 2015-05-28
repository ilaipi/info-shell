package com.richeninfo.xrzgather.kernel;

import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import java.util.List;

/**
 *
 * @author mz
 * @date 2013-8-8 9:45:09
 */
public class NetConfig {
    private ListPage listPage;
    private List<Page> pages;
    private Boolean hasInfoNameField = Boolean.FALSE;

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public ListPage getListPage() {
        return listPage;
    }

    public void setListPage(ListPage listPage) {
        this.listPage = listPage;
    }

    public Boolean getHasInfoNameField() {
        if(hasInfoNameField == null){
            hasInfoNameField = Boolean.FALSE;
        }
        return hasInfoNameField;
    }

    public void setHasInfoNameField(Boolean hasInfoNameField) {
        this.hasInfoNameField = hasInfoNameField;
    }
}
