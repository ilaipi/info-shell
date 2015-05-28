package com.richeninfo.xrzgather.kernel.stopmechanism;

/**
 * <pre>
 * 如果传入的count > 0，那么返回 StopCode.CONTINUE
 * 否则返回 StopCode.NORMAL
 * </pre>
 * @author mz
 * @date 2013-8-14 13:42:41
 */
public class IfExistsContinue extends StopMechanism{
    public StopCode stop(String infoTitle, Integer count){
        if(count != null && count > 0){
            setStopCode(StopCode.CONTINUE.setReason("标题[" + infoTitle + "]已采集"));
        } else{
            setStopCode(StopCode.NORMAL);
        }
        return getStopCode();
    }
}
