package com.richeninfo.xrzgather.kernel.field.fieldvalue;

import com.richeninfo.http.utils.HttpOperationUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author mz
 * @date 2013-8-8 13:45:16
 */
public class SubURL extends FieldValue{

    @Override
    public String getValue() {
        Element e = Jsoup.parse(locateHtml);
        Element linkElement = e.select("a").first();
        return HttpOperationUtils.getAbsoluteUrl(pageUrl, linkElement.attr("href"));
    }
    
}
