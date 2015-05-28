package com.richeninfo.xrzgather.kernel.resulthandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author mz
 * @date 2013-8-9 11:25:25
 */
public class ToDateHandler extends ResultHandler{
    private String format;

    @Override
    public void handle(Map<String, Object> currResult) {
        if(currResult.containsKey(key)){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                Date date = sdf.parse((String)currResult.get(key));
                currResult.put(key, date);
                
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
