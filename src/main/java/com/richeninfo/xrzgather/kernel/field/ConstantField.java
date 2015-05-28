package com.richeninfo.xrzgather.kernel.field;

/**
 *
 * @author mz
 * @date 2013-8-13 15:08:00
 */
public class ConstantField extends GlobalField{
    private String value;

    @Override
    public String getValue(String html) {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
