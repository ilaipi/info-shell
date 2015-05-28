package com.richeninfo.xrzgather.shell;

import com.richeninfo.http.DefaultHttpOperation;

import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * 或者url中有startDate和endDate，或者请求参数中有。
 * 在翻页请求时，会对url和请求参数做日期的替换。
 * 循环实现分页，开发原型是中国学术会议在线(http://www.meeting.edu.cn/meeting/meeting/notice/noticelistS.jsp不能直接用此链接，必须用查询时调用的那个post请求)。
 * </pre>
 * @author mz
 * @date 2013-8-8 9:46:19
 */
public class BeginAndEndDateShell extends CommonShell{
    protected int incremental;
    protected Integer increaseField;
    protected Date endDate;
    
    @Override
    protected Date getEndDate() {
        if(this.increaseField == null){
            return super.getEndDate();
        }
        Calendar cal = Calendar.getInstance();
    	cal.setTime(getStartDate());
    	cal.add(this.increaseField, this.incremental);
        this.endDate = cal.getTime();
    	return this.endDate;
    }

    public BeginAndEndDateShell() {
        this.operation = new DefaultHttpOperation();
    }

    public int getIncremental() {
        return incremental;
    }

    public void setIncremental(int incremental) {
        this.incremental = incremental;
    }

    public int getIncreaseField() {
        return increaseField;
    }

    public void setIncreaseField(int increaseField) {
        this.increaseField = increaseField;
    }
}
