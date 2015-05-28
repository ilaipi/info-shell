package com.richeninfo.xrzgather.dao;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author mz
 * @date 2013-8-16 16:48:02
 */
public interface GatherTechnologyDao extends GatherDao{
    public int techExists(Map<String, Object> result);
    public String getTechSuitableTitle(Map<String, Object> result);
    public void saveTechGatherResult(Map<String, Object> result);
    public List<Map<String, String>> searchInfoList(Map<String, String> result);
    public String showInfoText(String uuid);
}
