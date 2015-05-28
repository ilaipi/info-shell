package com.richeninfo.xrzgather.kernel.field;

import com.richeninfo.xrzgather.kernel.field.fieldvalue.FieldValue;
import com.richeninfo.xrzgather.kernel.field.handler.FieldValueHandler;
import com.richeninfo.xrzgather.kernel.locate.Locate;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mz
 * @date 2013-8-8 10:27:01
 */
public class Field {
    protected String name;
    protected String displayName;
    protected Locate locate;
    protected FieldValue fieldValue;
    protected FieldValueHandler<String> handler;
    
    public String getValue(String html){
        if(locate != null){
            html = locate.locate(html);
        }
        if(StringUtils.isNotBlank(html)){
            html = html.replace("&nbsp;", " ");
            html = html.trim();
        }
        String value = html;
        if(fieldValue != null){
            fieldValue.setLocateHtml(html);
            value = fieldValue.getValue();
        }
        if(handler != null){
            value = handler.handle(value);
        }
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Locate getLocate() {
        return locate;
    }

    public void setLocate(Locate locate) {
        this.locate = locate;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(FieldValue fieldValue) {
        this.fieldValue = fieldValue;
    }

    public FieldValueHandler getHandler() {
        return handler;
    }

    public void setHandler(FieldValueHandler handler) {
        this.handler = handler;
    }
}
