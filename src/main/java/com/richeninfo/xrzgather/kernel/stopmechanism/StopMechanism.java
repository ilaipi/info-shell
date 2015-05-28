package com.richeninfo.xrzgather.kernel.stopmechanism;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 由于各种停止机制需要的参数不通，所以不能定义接口，每个实现类最终都要设置停止代码。
 * @author mz
 * @date 2013-8-8 9:44:54
 */
public abstract class StopMechanism {
    protected Log logger = LogFactory.getLog(this.getClass());
    /**
     * 默认值为 StopCode.NORMAL
     * @see StopCode#NORMAL
     */
    protected StopCode stopCode;

    protected StopCode setStopCode(StopCode stopCode) {
        this.stopCode = stopCode;
        return this.stopCode;
    }

    public StopCode getStopCode() {
        if(stopCode == null){
            stopCode = StopCode.NORMAL;
        }
        return stopCode;
    }
}
