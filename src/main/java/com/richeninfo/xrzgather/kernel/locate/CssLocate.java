package com.richeninfo.xrzgather.kernel.locate;

import jodd.jerry.Jerry;

/**
 *
 * @author mz
 * @date 2013-8-8 12:26:32
 */
public class CssLocate extends Locate{
    private String cssSelector;
    
    @Override
    public String locate(String html){
        Jerry j = Jerry.$(cssSelector, Jerry.jerry(html));
        if(j.size() < 1){
            throw new IllegalArgumentException("指定选择器不能定位到任何标签。cssSelector:" + getCssSelector() + "。尝试使用其它定位方式");
        }
        return j.get(0).getHtml();
    }

    public String getCssSelector() {
        return cssSelector;
    }

    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }
}
