package com.richeninfo.xrzgather.kernel.resulthandler;

import java.util.Map;

/**
 *
 * @author mz
 * @date 2013-8-9 10:44:17
 */
public class ReplaceHandler extends ResultHandler{
    private String toReplace;
    private Boolean isReg;
    private String replaceWith;
    @Override
    public void handle(Map<String, Object> currResult) {
        String value = (String) currResult.get(key);
        com.richeninfo.xrzgather.kernel.field.handler.ReplaceHandler handler = new com.richeninfo.xrzgather.kernel.field.handler.ReplaceHandler();
        handler.setIsReg(isReg);
        handler.setReplaceWith(replaceWith);
        handler.setToReplace(toReplace);
        currResult.put(key, handler.handle(value));
    }

    public String getToReplace() {
        return toReplace;
    }

    public void setToReplace(String toReplace) {
        this.toReplace = toReplace;
    }

    public Boolean getIsReg() {
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
