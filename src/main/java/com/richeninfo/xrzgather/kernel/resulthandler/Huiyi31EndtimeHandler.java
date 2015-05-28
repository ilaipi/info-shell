package com.richeninfo.xrzgather.kernel.resulthandler;

import com.richeninfo.common.util.DateUtils;
import com.richeninfo.xrzgather.kernel.Info;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author mz
 * @date 2013-8-9 10:45:12
 */
public class Huiyi31EndtimeHandler extends ResultHandler{

    @Override
    public void handle(Map<String, Object> currResult) {
        String startYear = (String) currResult.get("startYear");
        String startMonth = (String) currResult.get("startMonth");
        String startDay = (String) currResult.get("startDay");
        
        String endTime = (String) currResult.get("endTime");
        String format;
        if(endTime.contains(" ")){
    		endTime = startYear + "/" + endTime;
    		format = "yyyy/M/d HH:mm";
        } else{
            endTime = startYear + "年" + startMonth + startDay + " " + endTime;
            format = "yyyy年M月d HH:mm";
        }
        Date beginDate = (Date) currResult.get(Info.INFO_PUBDATE.name());
        Date endDate = DateUtils.parse(endTime, format);
        
        if(beginDate.after(endDate)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.YEAR, 1);
            endDate = calendar.getTime();
        }
        
        currResult.put(key, endDate);
    }
}
