package com.richeninfo.xrzgather.web;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import com.richeninfo.xrzgather.dao.HuiyiDao;
import com.richeninfo.xrzgather.kernel.NetConfig;
import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.FieldValue;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.PlainText;
import com.richeninfo.xrzgather.kernel.field.handler.Hanzi2NumHandler;
import com.richeninfo.xrzgather.kernel.iterator.InfoIterator;
import com.richeninfo.xrzgather.kernel.iterator.JsoupIterator;
import com.richeninfo.xrzgather.kernel.locate.CssByJsoupLocate;
import com.richeninfo.xrzgather.kernel.locate.CssLocate;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import com.richeninfo.xrzgather.kernel.resulthandler.Huiyi31EndtimeHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.JoinResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.ResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.ToDateHandler;
import com.richeninfo.xrzgather.shell.URLSingleDateShell;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mz
 * @date 2013-8-9 17:52:04
 */
public class TestRun extends MultiActionController {
    private HuiyiDao huiyiDao;
    public ModelAndView testByHtml(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String html = request.getParameter("html");
        String jsoupCssSelector = request.getParameter("jsoupCssSelector");
        String cssSelector = request.getParameter("cssSelector");
        CssLocate locate = null;
        if(StringUtils.isNotBlank(cssSelector)){
            locate = new CssLocate();
            locate.setCssSelector(cssSelector);
        } else if(StringUtils.isNotBlank(jsoupCssSelector)){
            locate = new CssByJsoupLocate();
            locate.setCssSelector(jsoupCssSelector);
        } else{
            throw new IllegalArgumentException("两种选择器不能同时为空。cssSelector{" + cssSelector + "} jsoupCssSelector{" + jsoupCssSelector + "}");
        }
        ModelAndView modelAndView = new ModelAndView("flowDescView");
        modelAndView.addObject("flowDesc", locate.locate(html));
        return modelAndView;
    }
    public ModelAndView testListTag(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String url = request.getParameter("url");
        String cssSelector = request.getParameter("listCssSelector");
        
        System.out.println("url:" + url);
        System.out.println("selector:" + cssSelector);
        HttpOperation operation = new DefaultHttpOperation();
        String html = operation.get(url, null, null);
        InfoIterator iterator = new JsoupIterator();
        iterator = iterator.build(html, cssSelector);
        if(iterator.size() > 0){
            html = iterator.next();
        } else{
            html = "没有定位到!";
        }
        ModelAndView modelAndView = new ModelAndView("flowDescView");
        modelAndView.addObject("flowDesc", html);
        return modelAndView;
    }
    public ModelAndView testByUrl(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String url = request.getParameter("url");
        String jsoupCssSelector = request.getParameter("jsoupCssSelector");
        String cssSelector = request.getParameter("cssSelector");
        CssLocate locate = null;
        if(StringUtils.isNotBlank(cssSelector)){
            locate = new CssLocate();
            locate.setCssSelector(cssSelector);
        } else if(StringUtils.isNotBlank(jsoupCssSelector)){
            locate = new CssByJsoupLocate();
            locate.setCssSelector(jsoupCssSelector);
        } else{
            throw new IllegalArgumentException("两种选择器不能同时为空。cssSelector{" + cssSelector + "} jsoupCssSelector{" + jsoupCssSelector + "}");
        }
        ModelAndView modelAndView = new ModelAndView("flowDescView");
        HttpOperation operation = new DefaultHttpOperation();
        String html = operation.get(url, null, null);
        modelAndView.addObject("flowDesc", locate.locate(html));
        return modelAndView;
    }
    public ModelAndView run(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
        shell.setIncremental(0);
        shell.setResultHandler(resultHandlers);
        shell.setListPageUrl("http://www.31huiyi.com/calendar/datetime_$date{yyyy-MM-dd}_type_day");
        shell.execute(netConfig);
        List<Map<String, Object>> result = shell.getResult();
        System.out.println("size:" + result.size());
        Map<String, StringBuffer> existsHuiyi = new HashMap<String, StringBuffer>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> map : result){
            String meetingName = (String) map.get("INFO_TITLE");
            Date beginTime = (Date) map.get("startDateTime");
            int exists = huiyiDao.exists(beginTime, meetingName);
            switch(exists){
            case 0:
                huiyiDao.save(map);
                break;
            case 1:
                //logger.info("会议已存在，不再采集");
                System.out.println("会议已存在，不再采集");
                continue;
            case 2:
                StringBuffer buffer = existsHuiyi.remove(meetingName);
                if (buffer == null) {
                    buffer = new StringBuffer();
                }
                buffer.append(sdf.format(beginTime)).append(",");
                existsHuiyi.put(meetingName, buffer);
                continue;
            }
        }
        return null;
    }

    public HuiyiDao getHuiyiDao() {
        return huiyiDao;
    }

    public void setHuiyiDao(HuiyiDao huiyiDao) {
        this.huiyiDao = huiyiDao;
    }
}
