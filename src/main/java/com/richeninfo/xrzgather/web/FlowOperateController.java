package com.richeninfo.xrzgather.web;

import com.richeninfo.xrzgather.dao.GatherFlowDao;
import com.richeninfo.xrzgather.hbean.FlowTask;
import com.richeninfo.xrzgather.hbean.GatherFlow;
import com.richeninfo.xrzgather.shell.Shell;
import com.richeninfo.xrzgather.utils.ConfigTaskUtil;
import com.richeninfo.xrzgather.utils.FlowConfigUtil;
import com.richeninfo.xrzgather.utils.GatherRunner;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author mz
 * @date 2013-8-11 15:20:42
 */
public class FlowOperateController extends MultiActionController{
    private GatherFlowDao gatherFlowDao;
    /**
     * 处理保存流程的请求
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String flowConfig = request.getParameter("flowConfig");
        Shell shell = FlowConfigUtil.getShellConfig(flowConfig);
        String _UUID = request.getParameter("UUID");
        GatherFlow flow = new GatherFlow();
        Integer version = 1;
        if(StringUtils.isNotBlank(_UUID)){
            flow = gatherFlowDao.getGatherFlowByUUID(_UUID);
            gatherFlowDao.updateStatus(_UUID, GatherFlow.OLDER_VERSION_STATUS, flow.getVersion());//更新状态
            version = flow.getVersion() + 1;
        } else{
            _UUID = UUID.randomUUID().toString();
        }
        
        String requestHeaders = request.getParameter("requestHeaders");
        String requestBodys = request.getParameter("requestBodys");
        
        flow.setRequestBodys(requestBodys);
        flow.setRequestHeaders(requestHeaders);
        
        flow.setStatus(GatherFlow.LATEST_VERSION_STATUS);
        flow.setVersion(version);
        flow.setFlowDesc(flowConfig);
        flow.setNetName(shell.getNetName());
        flow.setListPageUrl(shell.getListPageUrl());
        flow.setGatherPrograma(shell.getGatherPrograma());
        flow.setUUID(_UUID);
        flow.setUpdateTime(new Date());
        gatherFlowDao.saveFlow(flow);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("success", true);
        modelAndView.addObject("msg", "保存成功！");
        modelAndView.addObject("FLOW_UUID", flow.getUUID());
        return modelAndView;
    }
    public ModelAndView clone(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String _UUID = request.getParameter("UUID");
        String flowConfig = gatherFlowDao.getFlowDesc(_UUID);
        GatherFlow flow = gatherFlowDao.getGatherFlowByUUID(_UUID);
        GatherFlow cloneFlow = (GatherFlow) BeanUtils.cloneBean(flow);
        cloneFlow.setGatherPrograma(flow.getGatherPrograma() + "(clone)");
        cloneFlow.setFlowDesc(flowConfig.replace("name=\"gatherPrograma\" value=\"" + flow.getGatherPrograma() + "\">", "name=\"gatherPrograma\" value=\"" + cloneFlow.getGatherPrograma() + "\">"));
        cloneFlow.setUUID(UUID.randomUUID().toString());
        cloneFlow.setId(null);
        gatherFlowDao.saveFlow(cloneFlow);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("success", true);
        modelAndView.addObject("msg", "复制成功！");
        modelAndView.addObject("FLOW_UUID", cloneFlow.getUUID());
        return modelAndView;
    }
    
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String _UUID = request.getParameter("UUID");
        gatherFlowDao.deleteGatherFlow(_UUID);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("success", true);
        modelAndView.addObject("msg", "删除成功！");
        return modelAndView;
    }
    
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String _UUID = request.getParameter("UUID");
        try {
            GatherRunner.run(_UUID);
        } catch (Exception e) {
            logger.warn(e);
        }
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("success", true);
        modelAndView.addObject("msg", "调试结束");
        return modelAndView;
    }
    
    public ModelAndView getFlowInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String UUID = request.getParameter("UUID");
        GatherFlow flow = gatherFlowDao.getFlowInfo(UUID);
        String requestHeaders = flow.getRequestHeaders();
        String requestBodys = flow.getRequestBodys();
        if(StringUtils.isBlank(requestBodys)){
            requestBodys = "";
        }
        if(StringUtils.isBlank(requestHeaders)){
            requestHeaders = "";
        }
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject("requestHeaders", requestHeaders);
        modelAndView.addObject("requestBodys", requestBodys);
        return modelAndView;
    }
    
    /**
     * 处理获取流程的xml描述的请求
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    public ModelAndView getFlowDesc(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String UUID = request.getParameter("UUID");
        if(StringUtils.isBlank(UUID)){
            return null;
        }
        String flowDesc = gatherFlowDao.getFlowDesc(UUID);
        if(StringUtils.isBlank(flowDesc)){
            flowDesc = "";
        }
        
        ModelAndView modelAndView = new ModelAndView("flowDescView");
        modelAndView.addObject("flowDesc", flowDesc);
        return modelAndView;
    }
    
    /**
     * 处理获取流程列表的请求
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    public ModelAndView getFlowList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String keyword = request.getParameter("keyword");
        if(StringUtils.isNotBlank(keyword)){
            keyword = "%" + keyword + "%";
        }
        List<GatherFlow> gatherFlows = gatherFlowDao.getGatherFlowList(keyword);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        int count = CollectionUtils.isEmpty(gatherFlows) ? 0 : gatherFlows.size();
        modelAndView.addObject("count", count);
        modelAndView.addObject("data", gatherFlows);
        return modelAndView;
    }
    
    public ModelAndView flowTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String uuid = request.getParameter("gatherFlowUUID");
        List<FlowTask> tasks = gatherFlowDao.getFlowTaskList(uuid);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        modelAndView.addObject(tasks);
        return modelAndView;
    }
    
    public ModelAndView deleteFlowTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String taskIdStr = request.getParameter("taskId");
        Integer taskId = Integer.parseInt(taskIdStr);
        String flowUUID = gatherFlowDao.getTaskFlowUUID(taskId);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        try{
            ConfigTaskUtil.pauseTrigger(flowUUID, taskId);
        } catch(Exception e){
            logger.warn("停止trigger失败", e);
            modelAndView.addObject("msg", "停止trigger失败！");
            modelAndView.addObject("success", false);
            return modelAndView;
        }
        modelAndView.addObject("msg", "删除成功！");
        modelAndView.addObject("success", true);
        gatherFlowDao.deleteFlowTask(taskId);
        return modelAndView;
    }
    
    public ModelAndView configTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String _UUID = request.getParameter("_UUID");
        String taskName = request.getParameter("taskName");
        String cron = request.getParameter("cron");
        FlowTask task = new FlowTask();
        task.setCron(cron);
        task.setFlowUUID(_UUID);
        task.setTaskName(taskName);
        ModelAndView modelAndView = new ModelAndView("jsonView");
        gatherFlowDao.saveFlowTask(task);
        try{
            ConfigTaskUtil.schedulerJob(task);
        } catch(Exception e){
            gatherFlowDao.deleteFlowTask(task.getId());
            logger.warn("调度任务出现异常", e);
            modelAndView.addObject("success", false);
            modelAndView.addObject("value", task.getId());
            return modelAndView;
        }
        modelAndView.addObject("success", true);
        modelAndView.addObject("value", task.getId());
        return modelAndView;
    }

    public void setGatherFlowDao(GatherFlowDao gatherFlowDao) {
        this.gatherFlowDao = gatherFlowDao;
    }
}
