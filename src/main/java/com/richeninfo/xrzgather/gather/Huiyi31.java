package com.richeninfo.xrzgather.gather;

import com.richeninfo.xrzgather.kernel.NetConfig;
import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.FieldValue;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.PlainText;
import com.richeninfo.xrzgather.kernel.field.handler.Hanzi2NumHandler;
import com.richeninfo.xrzgather.kernel.locate.CssLocate;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import com.richeninfo.xrzgather.kernel.resulthandler.ResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.Huiyi31EndtimeHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.JoinResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.ToDateHandler;
import com.richeninfo.xrzgather.shell.URLSingleDateShell;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author mz
 * @date 2013-8-8 14:49:54
 */
public class Huiyi31 {
    public static void main(String[] args) {
        Field startYear = new Field();
        CssLocate startYearLocate = new CssLocate();
        startYearLocate.setCssSelector("div.lefttime p.year");
        FieldValue startYearFieldValue = new PlainText();
        startYear.setLocate(startYearLocate);
        startYear.setFieldValue(startYearFieldValue);
        startYear.setName("startYear");
        
        Field startMonth = new Field();
        CssLocate startMonthLocate = new CssLocate();
        startMonthLocate.setCssSelector("div.lefttime p.month");
        FieldValue startMonthFieldValue = new PlainText();
        startMonth.setLocate(startMonthLocate);
        startMonth.setFieldValue(startMonthFieldValue);
        startMonth.setName("startMonth");
        Hanzi2NumHandler startMonthHandler = new Hanzi2NumHandler();
        startMonth.setHandler(startMonthHandler);
        
        Field startDay = new Field();
        CssLocate startDayLocate = new CssLocate();
        startDayLocate.setCssSelector("div.lefttime p.day");
        FieldValue startDayFieldValue = new PlainText();
        startDay.setLocate(startDayLocate);
        startDay.setFieldValue(startDayFieldValue);
        startDay.setName("startDay");
        
        
        Field startTime = new Field();
        CssLocate startTimeLocate = new CssLocate();
        startTimeLocate.setCssSelector("p.week font.year");
        FieldValue startTimeFieldValue = new PlainText();
        startTime.setLocate(startTimeLocate);
        startTime.setFieldValue(startTimeFieldValue);
        startTime.setName("startTime");
        
        Field endTime = new Field();
        CssLocate endTimeLocate = new CssLocate();
        endTimeLocate.setCssSelector("p.week font.year.over");
        FieldValue endTimeFieldValue = new PlainText();
        endTime.setLocate(endTimeLocate);
        endTime.setFieldValue(endTimeFieldValue);
        endTime.setName("endTime");
        
        Field place = new Field();
        CssLocate placeLocate = new CssLocate();
        placeLocate.setCssSelector(".address");
        FieldValue placeFieldValue = new PlainText();
        place.setLocate(placeLocate);
        place.setFieldValue(placeFieldValue);
        place.setName("place");
        
        Page page = new Page();
        List<Field> fields = new ArrayList<Field>();
        fields.add(startYear);
        fields.add(startMonth);
        fields.add(startDay);
        fields.add(place);
        fields.add(startTime);
        fields.add(endTime);
        CssLocate pageLocate = new CssLocate();
        pageLocate.setCssSelector(".module_content-baoming");
        page.setLocate(pageLocate);
        page.setFields(fields);
        
        List<Page> pages = new ArrayList<Page>();
        pages.add(page);
        
        ListPage listPage = new ListPage();
        listPage.setListCssSelector(".myeventlist li");
        listPage.setLinkCssSelector("a:nth-child(1)");
        
        NetConfig netConfig = new NetConfig();
        netConfig.setListPage(listPage);
        netConfig.setPages(pages);
        
        List<ResultHandler> resultHandlers = new ArrayList<ResultHandler>();
        
        JoinResultHandler startDateTimeHandler = new JoinResultHandler();
        startDateTimeHandler.setKey("startDateTime");
        startDateTimeHandler.setJoinKeys("${startYear}$C{年}${startMonth}${startDay}$C{ }${startTime}");
        resultHandlers.add(startDateTimeHandler);
        
        ToDateHandler toDateHandler = new ToDateHandler();
        toDateHandler.setKey("startDateTime");
        toDateHandler.setFormat("yyyy年M月d HH:mm");
        resultHandlers.add(toDateHandler);
        
        Huiyi31EndtimeHandler endDateTimeHandler = new Huiyi31EndtimeHandler();
        endDateTimeHandler.setKey("endDateTime");
        resultHandlers.add(endDateTimeHandler);
        
        
        
        URLSingleDateShell shell = new URLSingleDateShell();
        shell.setIncreaseField(Calendar.DAY_OF_MONTH);
        shell.setIncremental(1);
        shell.setResultHandler(resultHandlers);
        shell.setListPageUrl("http://www.31huiyi.com/calendar/datetime_$date{yyyy-MM-dd}_type_day");
        shell.execute(netConfig);
    }
}
