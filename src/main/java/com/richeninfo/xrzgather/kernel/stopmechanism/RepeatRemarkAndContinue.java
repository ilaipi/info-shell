package com.richeninfo.xrzgather.kernel.stopmechanism;

import com.richeninfo.common.util.DateUtils;
import com.richeninfo.xrzgather.ApplicationHelper;
import com.richeninfo.xrzgather.dao.GatherHuiyiDao;
import com.richeninfo.xrzgather.kernel.Info;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * 注：目前只适用于 会议类的资讯
 * <pre>
 * 如果传入的startDate 与 meetingName在数据库中已有的记录的begin_time不同一天，则返回 StopCode.CONTINUE。同时把remark字段更新
 * 否则返回 StopCode.NORMAL
 * </pre>
 * @author mz
 * @date 2013-8-15 18:01:26
 */
public class RepeatRemarkAndContinue extends StopMechanism{
    public StopCode stop(Map<String, String> currResult, Date startDate){
        //1. 查询数据库中的日期
        DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) ApplicationHelper.getInstance().getBean("sqlSessionFactory");
        SqlSession session = sqlSessionFactory.openSession();
        try{
            GatherHuiyiDao gatherHuiyiDao = session.getMapper(GatherHuiyiDao.class);
            Map<String, Object> map = gatherHuiyiDao.huiyiBeginDate(currResult);
            if(MapUtils.isEmpty(map)){
                return getStopCode();
            }
            String remark = (String) map.get("remark");
            Date beginDate = (Date) map.get("begin_time");
            String startDateStr = DateUtils.format(startDate, "yyyy-MM-dd");
            String infoTitle = currResult.get(Info.INFO_TITLE.name());
            if(!DateUtils.format(beginDate, "yyyy-MM-dd").equals(startDateStr)){
                if(StringUtils.isNotBlank(remark)){
                    if(remark.contains(startDateStr)){
                        setStopCode(StopCode.CONTINUE.setReason("标题[" + infoTitle + "]已采集"));
                        return getStopCode();
                    }
                    remark += "," + startDateStr;
                } else{
                    remark = startDateStr;
                }
                Map<String, Object> selectParam = new HashMap<>();
                selectParam.put("remark", remark);
                selectParam.put(Info.INFO_TITLE.name(), infoTitle);
                selectParam.put("infoSource", currResult.get("infoSource"));
                gatherHuiyiDao.updateHuiyiRemark(selectParam);
            }
            setStopCode(StopCode.CONTINUE.setReason("标题[" + infoTitle + "]已采集"));
        } finally{
            session.close();
        }
        return getStopCode();
    }
}
