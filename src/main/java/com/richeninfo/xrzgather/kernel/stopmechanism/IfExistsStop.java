package com.richeninfo.xrzgather.kernel.stopmechanism;

/**
 * <pre>
 * 如果传入的count > 0，那么返回 StopCode.STOP
 * 否则返回 StopCode.NORMAL
 * </pre>
 * @author mz
 * @date 2013-8-14 13:35:59
 */
public class IfExistsStop extends StopMechanism{
    public StopCode stop(String infoTitle, Integer count){
        if(count != null && count > 0){
            setStopCode(StopCode.STOP.setReason("标题[" + infoTitle + "]已采集"));
        } else{
            setStopCode(StopCode.NORMAL);
        }
        return getStopCode();
    }
}
