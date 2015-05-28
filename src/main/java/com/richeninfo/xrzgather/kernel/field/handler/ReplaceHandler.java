package com.richeninfo.xrzgather.kernel.field.handler;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mz
 * @date 2013-8-19 13:43:52
 */
public class ReplaceHandler extends FieldValueHandler<String>{
    private String toReplace;
    private Boolean isReg;
    private String replaceWith;

    @Override
    public String handle(String value) {
        if(value == null){
            return "";
        }
        if(isReg == null || isReg){
            value = value.replaceAll(toReplace, replaceWith);
        } else{
            String[] replaces = new String[]{toReplace};
            if(toReplace.contains(" ")){//多字符串替换
                replaces = StringUtils.split(toReplace);
            }
            String[] replaceWiths = new String[]{replaceWith};
            if(replaceWith.contains(" ")){
                replaceWiths = StringUtils.split(replaceWith);
            }
            //length相等
            if(replaces.length == replaceWiths.length){
                for(int i = 0;i<replaces.length;i++){
                    value = StringUtils.replaceOnce(value, replaces[i], replaceWiths[i]);
                }
                return value;
            }
            //length不相等。replaceWiths length = 1。所有的replaces都替换为replaceWiths[1]
            if(replaceWiths.length == 1 && replaces.length > 1){
                for(int i = 0;i<replaces.length;i++){
                    value = StringUtils.replaceOnce(value, replaces[i], replaceWiths[0]);
                }
                return value;
            }
            if(replaceWiths.length != replaces.length){
                throw new IllegalArgumentException("要替换的字符串 的个数和 替换为 的个数不同");
            }
        }
        return value;
    }

    public String getToReplace() {
        return toReplace;
    }

    public void setToReplace(String toReplace) {
        this.toReplace = toReplace;
    }

    public Boolean getIsReg() {
        if(isReg == null){
            isReg = Boolean.FALSE;
        }
        return isReg;
    }

    public void setIsReg(Boolean isReg) {
        this.isReg = isReg;
    }

    public String getReplaceWith() {
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }
    
}
