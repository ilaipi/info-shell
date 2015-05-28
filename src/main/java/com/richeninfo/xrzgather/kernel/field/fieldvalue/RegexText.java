package com.richeninfo.xrzgather.kernel.field.fieldvalue;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mz
 * @date 2013-8-8 13:52:59
 */
public class RegexText extends FieldValue{
    private String regx;
    private Integer index;

    @Override
    public String getValue() {
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(locateHtml);
        if(m.find()){
            if(null == index || 0 == index){
                return m.group();
            }
            if(m.groupCount() >= index){
                return m.group(index);
            }
        }
        return "";
    }
    
    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().length());
    }

    public String getRegx() {
        return regx;
    }

    public void setRegx(String regx) {
        this.regx = regx;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

}
