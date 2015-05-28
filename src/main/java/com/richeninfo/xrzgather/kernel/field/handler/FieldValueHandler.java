package com.richeninfo.xrzgather.kernel.field.handler;

/**
 *
 * @author mz
 * @date 2013-8-8 17:14:27
 */
public abstract class FieldValueHandler<T> {
    
    public abstract T handle(String value);
}
