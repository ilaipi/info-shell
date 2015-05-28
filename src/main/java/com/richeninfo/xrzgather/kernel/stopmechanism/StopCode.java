package com.richeninfo.xrzgather.kernel.stopmechanism;

/**
 * 停止代码。程序根据不同代码做不同的响应
 * @author mz
 * @date 2013-8-14 13:29:40
 */
public enum StopCode {
    /**
     * 进行下一次循环
     */
    CONTINUE,
    /**
     * 停止本次采集
     */
    STOP,
    /**
     * 正常，继续当前操作
     */
    NORMAL;
    
    private String reason;
    
    public StopCode setReason(String reason){
        this.reason = reason;
        return this;
    }
    
    public String getReason(){
        return reason;
    }
}
