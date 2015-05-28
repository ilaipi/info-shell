package com.richeninfo.xrzgather.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author mz
 * @date 2013-8-18 12:48:05
 */
public interface GatherPolicyDao extends GatherDao{
    public int policyExists(Map<String, Object> result);
    public String getPolicySuitableTitle(Map<String, Object> result);
    public void savePolicyGatherResult(Map<String, Object> result);
    public void savePolicyAttachment(Map<String, Object> result);
    public List<Map<String, String>> searchInfoList(Map<String, String> result);
    public String showInfoText(String uuid);
}
