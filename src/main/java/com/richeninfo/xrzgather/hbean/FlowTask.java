package com.richeninfo.xrzgather.hbean;

/**
 *
 * @author mz
 * @date 2013-8-13 9:12:47
 */
public class FlowTask {
    private Integer id;
    private String flowUUID;
    private String cron;
    private String taskName;//用于对一个流程多个任务进行区分

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlowUUID() {
        return flowUUID;
    }

    public void setFlowUUID(String flowUUID) {
        this.flowUUID = flowUUID;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
