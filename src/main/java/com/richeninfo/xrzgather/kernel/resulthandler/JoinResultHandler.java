package com.richeninfo.xrzgather.kernel.resulthandler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mz
 * @date 2013-8-9 10:15:29
 */
public class JoinResultHandler extends ResultHandler{
    private String joinKeys;//${}${}$C{}

    @Override
    public void handle(Map<String, Object> currResult) {
        Pattern p = Pattern.compile("\\$([A-Z]?)\\{([^\\}]*)\\}");
        Matcher m = p.matcher(joinKeys);
        StringBuffer result = new StringBuffer();
        while(m.find()){
            String g1 = m.group(1);
            String g2 = m.group(2);
            if(StringUtils.isNotBlank(g1)){
                if("C".equals(g1)){
                    result.append(g2);
                    continue;
                }
            } else{
                if(currResult.containsKey(g2)){
                    result.append(currResult.get(g2));
                } else{
                    result.append("");
                }
            }
        }
        currResult.put(key, result.toString());
    }
    
    public static void main(String[] args) {
        Pattern p = Pattern.compile("\\$([A-Z]?)\\{([^\\}]*)\\}");
        Matcher m = p.matcher("${startYear}${startMonth}${startDay}$C{ }");
        while(m.find()){
            for(int i = 0;i<=m.groupCount();i++){
                System.out.println("group" + i + ":[" + m.group(i) + "]");
            }
        }
    }

    public String getJoinKeys() {
        return joinKeys;
    }

    public void setJoinKeys(String joinKeys) {
        this.joinKeys = joinKeys;
    }
}
