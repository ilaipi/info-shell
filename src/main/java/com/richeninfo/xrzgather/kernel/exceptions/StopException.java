package com.richeninfo.xrzgather.kernel.exceptions;

/**
 * 
 * @author mz
 * @date 2013-8-17 11:36:09
 */
public class StopException extends RuntimeException{

    public StopException() {
    }
    public StopException(String message){
        super(message);
    }
}
