package com.richeninfo.xrzgather.shell;

import com.richeninfo.xrzgather.kernel.NetConfig;
import java.util.Calendar;

/**
 * <pre>
 * 开发原型是：31会议网(http://www.31huiyi.com/calendar/datetime_2013-08-23_type_day)
 * url中有一个日期，每个日期只有一页的数据，每次采集需要采集一个时间段的数据。
 * 所以可以设置开始日期和增量，程序算出结束时间，从开始日期开始，每天即为一页。当开始日期等于结束日期时结束。
 * </pre>
 * @author mz
 * @date 2013-8-8 9:48:03
 */
public class URLSingleDateShell extends BeginAndEndDateShell{
    @Override
    public void loop(NetConfig netConfig) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDate());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        setStartDate(cal.getTime());
        while(getStartDate().before(endDate)){
            setCurrentPageNum(Integer.parseInt(getCurrentPageNum()) + 1 + "");
            beforeRequest(endDate);
            listPageHtml = requestListPage();
            gather(netConfig);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            setStartDate(cal.getTime());
        }
    }
}
