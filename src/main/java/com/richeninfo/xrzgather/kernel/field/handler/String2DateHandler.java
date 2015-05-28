package com.richeninfo.xrzgather.kernel.field.handler;

import java.util.Date;

import com.richeninfo.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mz
 * @date 2013-8-15 15:25:24
 */
public class String2DateHandler extends FieldValueHandler<String>{
    private String format;
    @Override
    public String handle(String value) {
        if(StringUtils.isBlank(value)){
            return "";
        }
        value = value.replace("T", " ");//处理包含时区的时间字符串中的T
        Date date = DateUtils.parse(value, format);
        return DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
