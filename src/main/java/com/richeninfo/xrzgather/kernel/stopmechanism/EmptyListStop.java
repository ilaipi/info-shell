package com.richeninfo.xrzgather.kernel.stopmechanism;

/**
 * <pre>
 * 如果传入空的list，那么返回 StopCode.STOP
 * 否则返回 StopCode.NORMAL
 * </pre>
 * @author mz
 * @date 2013-8-14 13:17:48
 */
public class EmptyListStop extends StopMechanism{
    public StopCode stop(int size){
        if(size < 1){
            setStopCode(StopCode.STOP.setReason("空的列表"));
        } else{
            setStopCode(StopCode.NORMAL);
        }
        return getStopCode();
    }
}
