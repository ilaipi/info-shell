package com.richeninfo.xrzgather.kernel.locate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 
 * @author mz
 * @date 2013-8-20 14:56:05
 */
public class CssByJsoupLocate extends CssLocate{
    @Override
    public String locate(String html){
        Document doc = Jsoup.parse(html);
        Elements find = doc.select(getCssSelector());
        if(find.size() < 1){
            throw new IllegalArgumentException("指定选择器不能定位到任何标签。cssSelector:" + getCssSelector() + "。尝试使用其它定位方式");
        }
        return find.get(0).outerHtml();
    }
}
