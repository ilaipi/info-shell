package com.richeninfo.xrzgather.kernel.field.fieldvalue;

import jodd.jerry.Jerry;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mz
 * @date 2013-8-8 13:51:51
 */
public class PlainText extends FieldValue{

    @Override
    public String getValue() {
        Jerry j = Jerry.jerry(locateHtml);
        if(j != null){
            locateHtml = j.text();
        }
        if(StringUtils.isNotEmpty(locateHtml)){
            locateHtml = locateHtml.trim();
        }
        return locateHtml;
    }

}
