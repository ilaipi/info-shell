package com.richeninfo.xrzgather.kernel.field.handler;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mz
 * @date 2013-8-15 17:36:46
 */
public class Huiyi31TitleHandler extends FieldValueHandler<String>{

    @Override
    public String handle(String value) {
        if(StringUtils.isBlank(value)){
            return "";
        }
        if(value.contains("-")){
            String[] names = value.split("\\-");
            StringBuffer buffer = new StringBuffer(names[0]);
            for(int i = 1;i<names.length;i++){
                if(StringUtils.isEmpty(names[i])){
                    buffer.append("-");
                    continue;
                }
                if(!names[i].trim().equals(names[i-1].trim())){
                    buffer.append("-").append(names[i]);
                }
            }
            value = buffer.toString();
            value = value.replaceAll("\\-{3,}", "--");
        }
        return value;
    }

}
