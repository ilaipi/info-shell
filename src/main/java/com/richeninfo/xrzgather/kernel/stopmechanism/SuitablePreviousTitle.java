package com.richeninfo.xrzgather.kernel.stopmechanism;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author mz
 * @date 2013-8-17 10:20:53
 */
public class SuitablePreviousTitle extends StopMechanism{
    public StopCode stop(String suitableTitle, String gatherTitle){
        if(StringUtils.isBlank(gatherTitle)){
            return setStopCode(StopCode.CONTINUE.setReason("标题为空，丢弃，继续采集下一个"));
        }
        if(suitableTitle.equals(gatherTitle)){
            return setStopCode(StopCode.STOP.setReason("标题[" + gatherTitle + "]已采集"));
        }
        return getStopCode();
    }
}
