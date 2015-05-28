package com.richeninfo.xrzgather.kernel.iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author mz
 * @date 2013-8-21 18:47:35
 */
public class JsoupIterator extends InfoIterator{

    @Override
    public void parse(String html, String listCssSelector) {
        Document document = Jsoup.parse(html);
        list = document.select(listCssSelector);
    }
    
    @Override
    public String getNextString() {
        String html = ((Element)list.get(index)).outerHtml();
        return html;
    }

    @Override
    public int size() {
        return list.size();
    }
    
    @Override
    public boolean hasSelectLink(String linkCssSelector) {
        Element obj = (Element) list.get(index);
        Elements find = obj.select(linkCssSelector);
        if(find.size() > 0){
            return true;
        }
        return false;
    }
}
