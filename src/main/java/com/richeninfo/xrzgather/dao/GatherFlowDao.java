package com.richeninfo.xrzgather.dao;

import com.richeninfo.xrzgather.hbean.FlowTask;
import com.richeninfo.xrzgather.hbean.GatherFlow;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 *
 * @author mz
 * @date 2013-8-11 15:40:05
 */
public class GatherFlowDao extends SqlSessionDaoSupport{
    public List<GatherFlow> getGatherFlowList(String keyword){
        return getSqlSession().selectList(this.getClass().getName() + ".select_gatherFlowList", keyword);
    }
    public List<FlowTask> getFlowTaskList(String _UUID){
        return getSqlSession().selectList(this.getClass().getName() + ".get_task_list", _UUID);
    }
    public GatherFlow getGatherFlowByUUID(String _UUID){
        return getSqlSession().selectOne(this.getClass().getName() + ".select_gather_flow_by_uuid", _UUID);
    }
    
    public GatherFlow getFlowInfo(String UUID){
        return getSqlSession().selectOne(this.getClass().getName() + ".select_gather_flow_info", UUID);
    }
    
    public String getFlowDesc(String UUID){
        return getSqlSession().selectOne(this.getClass().getName() + ".select_gather_flow_desc", UUID);
    }
    
    public String getTaskFlowUUID(int taskId){
        return getSqlSession().selectOne(this.getClass().getName() + ".get_task_flow_uuid", taskId);
    }
    
    public int deleteFlowTask(int taskId){
        return getSqlSession().delete(this.getClass().getName() + ".deleteTask", taskId);
    }
    
    public int deleteGatherFlow(String _UUID){
        return getSqlSession().delete(this.getClass().getName() + ".deleteFlow", _UUID);
    }
    
    public String saveFlow(GatherFlow gatherFlow){
        return getSqlSession().selectOne(this.getClass().getName() + ".save_gather_flow", gatherFlow);
    }
    
    public int saveFlowTask(FlowTask task){
        return getSqlSession().insert(this.getClass().getName() + ".save_flow_task", task);
    }
    
    public void updateStatus(String _UUID, String status, Integer version){
        if(StringUtils.isBlank(status)){
            status = GatherFlow.OLDER_VERSION_STATUS;//默认修改状态为 旧版本
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("_UUID", _UUID);
        param.put("status", status);
        param.put("version", version);
        getSqlSession().update(this.getClass().getName() + ".update_gather_flow_status", param);
    }
}
