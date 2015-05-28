package com.richeninfo.xrzgather.kernel.bean;

import java.util.Date;

/**
 *
 * @author mz
 * @date 2013-8-8 17:44:12
 */
public class Huiyi31Info{
    private int meetingId;//主键
	private String meetingName;//会议名称
	private String meetingPlace;//会议地点
	private Date beginTime;//会议开始时间
	private Date endTime;//会议结束时间
	private String meetingOrganizer;//会议主办单位
	private String meetingUndertaker;//会议承办单位
	private String meetingTopic;//议题简介
	private String infoSource;//资讯来源
	private String infoUrl;//资讯地址
	private Date updateTime;//更新时间
	private String remark;

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMeetingOrganizer() {
        return meetingOrganizer;
    }

    public void setMeetingOrganizer(String meetingOrganizer) {
        this.meetingOrganizer = meetingOrganizer;
    }

    public String getMeetingUndertaker() {
        return meetingUndertaker;
    }

    public void setMeetingUndertaker(String meetingUndertaker) {
        this.meetingUndertaker = meetingUndertaker;
    }

    public String getMeetingTopic() {
        return meetingTopic;
    }

    public void setMeetingTopic(String meetingTopic) {
        this.meetingTopic = meetingTopic;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
