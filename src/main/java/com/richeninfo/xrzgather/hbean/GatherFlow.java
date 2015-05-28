package com.richeninfo.xrzgather.hbean;

import java.util.Date;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author mz
 * @date 2013-8-11 15:25:13
 */
public class GatherFlow {
    public static final String LATEST_VERSION_STATUS = "latest";
    public static final String OLDER_VERSION_STATUS = "older";
    private Integer id;
    private String netName;//网站名称
    private String gatherPrograma;//采集栏目
    private String listPageUrl;//栏目的地址
    @JsonIgnore//转换json时过滤此属性
    private String flowDesc;//采集流程的xml描述
    private String UUID;//唯一标识一个采集流程
    @JsonIgnore
    private Integer version;//版本标识。每次保存，版本号+1，主要用于备份(如果某次乱改可以通过数据库改回去)
    @JsonIgnore
    private String status;//取值：old,new。每次保存，之前的都会被标记为old。方便读取最新的版本
    
    private Date updateTime;//版本为old的时候，根据updateTime排序可以知道哪个是上次编辑的
    
    @JsonIgnore
    private String requestHeaders;//请求头。格式为fiddler中检测到的
    @JsonIgnore
    private String requestBodys;//请求体。格式为fiddler中检测到的

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getGatherPrograma() {
        return gatherPrograma;
    }

    public void setGatherPrograma(String gatherPrograma) {
        this.gatherPrograma = gatherPrograma;
    }

    public String getListPageUrl() {
        return listPageUrl;
    }

    public void setListPageUrl(String listPageUrl) {
        this.listPageUrl = listPageUrl;
    }

    public String getFlowDesc() {
        return flowDesc;
    }

    public void setFlowDesc(String flowDesc) {
        this.flowDesc = flowDesc;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(String requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBodys() {
        return requestBodys;
    }

    public void setRequestBodys(String requestBodys) {
        this.requestBodys = requestBodys;
    }
}
