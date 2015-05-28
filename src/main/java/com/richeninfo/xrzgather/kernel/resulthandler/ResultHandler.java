package com.richeninfo.xrzgather.kernel.resulthandler;

import com.richeninfo.http.HttpOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 *
 * @author mz
 * @date 2013-8-8 17:42:20
 */
public abstract class ResultHandler {
    protected Log logger = LogFactory.getLog(this.getClass());
    protected String key;
    protected HttpOperation operation;
    protected Map<String, String> headers;
    
    public abstract void handle(Map<String, Object> currResult);
    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpOperation getOperation() {
        return operation;
    }

    public void setOperation(HttpOperation operation) {
        this.operation = operation;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
