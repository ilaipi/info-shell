package com.richeninfo.xrzgather.utils;

import java.text.SimpleDateFormat;
import org.apache.commons.beanutils.Converter;

/**
 * 当使用commons-beanutils的BeanUtils.setProperties方法时，先注册此类。
 * 注册后，支持 yyyy-MM-dd 类型的字符串转换为java.util.Date对象
 * @author mz
 * @date 2013-8-13 9:57:59
 */
public class BeanUtilsDateConvert implements Converter{

    @Override
    public Object convert(Class type, Object o) {
        String p = (String)o;
        if(p== null || p.trim().length()==0){  
            return null;  
        }  
        try{  
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
            return df.parse(p.trim());  
        }  
        catch(Exception e){  
            return null;  
        }  
    }

}
