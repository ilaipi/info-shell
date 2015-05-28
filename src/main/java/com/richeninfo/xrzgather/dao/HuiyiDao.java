package com.richeninfo.xrzgather.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 *
 * @author mz
 * @date 2013-8-9 17:40:39
 */
public class HuiyiDao extends SqlSessionDaoSupport{
    public int exists(Date beginTime, String meetingName){
        meetingName = transMeetingName(meetingName);
        List<Date> list = getSqlSession().selectList(this.getClass().getName() + ".select_exists", meetingName);
        if(CollectionUtils.isNotEmpty(list)){
            Date startDate = list.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(sdf.format(beginTime).equals(sdf.format(startDate))){
                return 1;
            } else{
                return 2;
            }
        }else{
            return 0;
        }
    }
    
    private String transMeetingName(String meetingName){
        if(meetingName.contains("-")){
            String[] names = StringUtils.split(meetingName, "-");
            StringBuffer buffer = new StringBuffer(names[0]);
            for(int i = 1;i<names.length;i++){
                if(!names[i].trim().equals(names[i-1].trim())){
                    buffer.append("-").append(names[i]);
                }
            }
            meetingName = buffer.toString();
        }
		return commonTrans(meetingName);
	}
    
    /**
     * 处理字段值中需要转义的字符
     * @param fieldValue
     * @return 
     */
    private String commonTrans(String fieldValue){
        fieldValue = fieldValue.trim();
        return fieldValue;
    }
    
    /**
     * 把会议地点处理一下，防止出现sql不能执行的现象
     * @param meetingPlace
     * @return 
     */
    private String transMeetingPlace(String meetingPlace){
        meetingPlace = meetingPlace.replace("d'd", "");//处理网页上偶尔出现的无效信息。如：http://www.31huiyi.com/event/65327/
        meetingPlace = meetingPlace.replaceAll("[\\s]{2,}", " ");
        return commonTrans(meetingPlace);
    }
    
    public void save(Map<String, Object> result){
        result.put("INFO_TITLE", transMeetingName(result.remove("INFO_TITLE").toString()));
        result.put("place", transMeetingPlace(result.remove("place").toString()));
        result.put("updatetime", new Date());
        getSqlSession().insert(this.getClass().getName() + ".save_gather_result", result);
    }

}
