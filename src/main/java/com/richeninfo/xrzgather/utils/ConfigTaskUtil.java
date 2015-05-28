package com.richeninfo.xrzgather.utils;

import com.richeninfo.xrzgather.ApplicationHelper;
import com.richeninfo.xrzgather.hbean.FlowTask;
import com.richeninfo.xrzgather.job.GatherJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdScheduler;

/**
 *
 * @author mz
 * @date 2013-8-13 20:53:29
 */
public class ConfigTaskUtil {
    public static final String TASK_GROUP = "XRZ_GATHER";
    
    public static String getTaskName(String UUID, Integer taskId){
        return TASK_GROUP + "_" + UUID + "_" + taskId;
    }
    
    public static void pauseTrigger(String UUID, Integer taskId) throws SchedulerException{
        //停止quartz的调度
        StdScheduler scheduler = (StdScheduler) ApplicationHelper.getInstance().getBean("schedulerFactory");
        //只需要停止掉trigger即可
        scheduler.pauseTrigger(TriggerKey.triggerKey(ConfigTaskUtil.getTaskName(UUID, taskId), ConfigTaskUtil.TASK_GROUP));
    }
    
    public static void schedulerJob(FlowTask task) throws SchedulerException{
        String taskName = getTaskName(task.getFlowUUID(), task.getId());
        StdScheduler scheduler = (StdScheduler) ApplicationHelper.getInstance().getBean("schedulerFactory");
        JobKey jobKey = JobKey.jobKey(taskName, TASK_GROUP);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if(jobDetail == null){
            jobDetail = JobBuilder.newJob(GatherJob.class).withIdentity(jobKey).build();
        }
        jobDetail.getJobDataMap().put("flowUUID", task.getFlowUUID());
        CronTrigger cronTrigger = (CronTrigger) TriggerBuilder.newTrigger().withIdentity(taskName, TASK_GROUP).withSchedule(CronScheduleBuilder.cronSchedule(task.getCron())).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }
}
