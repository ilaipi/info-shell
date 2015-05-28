package com.richeninfo.xrzgather.kernel.field.handler;

/**
 * 主要用于月份的转换
 * @author mz
 * @date 2013-8-8 17:14:11
 */
public class Hanzi2NumHandler extends FieldValueHandler<String>{
    private static String[] hanzi = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private static String[] num = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};


    @Override
    public String handle(String value) {
        for(int i = hanzi.length - 1;i>=0;i--){
            value = value.replace(hanzi[i], num[i]);
        }
        return value;
    }
}
