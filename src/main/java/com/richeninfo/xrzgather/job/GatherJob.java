package com.richeninfo.xrzgather.job;

import com.richeninfo.xrzgather.utils.GatherRunner;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author mz
 * @date 2013-8-13 9:10:45
 */
public class GatherJob implements Job{
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        String flowUUID = jec.getMergedJobDataMap().getString("flowUUID");
        GatherRunner.run(flowUUID);
    }
}
