package com.richeninfo.xrzgather.kernel.stopmechanism;

import com.richeninfo.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * <pre>
 * 如果other(2013-08-14) 小于 base(2013-08-15)，那么返回 StopCode.STOP
 * 否则返回 StopCode.NORMAL
 * </pre>
 * @author mz
 * @date 2013-8-15 10:12:50
 */
public class OlderDateStop extends StopMechanism{
    public StopCode stop(Date base, String other){
        String format = "yyyy-MM-dd";
        String baseDateStr = DateUtils.format(base, format);
        if(StringUtils.isBlank(other)){
            return setStopCode(StopCode.STOP.setReason("开始日期为空"));
        }
        other = other.replace("T", " ");//处理包含时区的时间字符串中的T
        String otherDateStr = "";
        try{
            otherDateStr = DateUtils.format(DateUtils.parse(other, "yyyy-MM-dd HH:mm:ss"), format);
        } catch(Exception e){
            logger.warn("不能识别的日期", e);
            return setStopCode(StopCode.CONTINUE.setReason("不能识别的开始日期。"));
        }
        
        Date baseDate = DateUtils.parse(baseDateStr, format);
        Date otherDate = DateUtils.parse(otherDateStr, format);
        
        if(otherDate.before(baseDate)){
            setStopCode(StopCode.STOP.setReason("早于开始日期"));
        }
        
        return getStopCode();
    }
}
