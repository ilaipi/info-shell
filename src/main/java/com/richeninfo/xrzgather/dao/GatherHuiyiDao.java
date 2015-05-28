package com.richeninfo.xrzgather.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @author mz
 * @date 2013-8-13 15:24:58
 */
public interface GatherHuiyiDao extends GatherDao{
    public int huiyiExists(Map<String, Object> result);
    public String getSuitableTitle(Map<String, Object> result);
    public void saveGatherResult(Map<String, Object> result);
    public Map<String, Object> huiyiBeginDate(Map<String, String> result);
    public void updateHuiyiRemark(Map<String, Object> selectParam);
    public List<Map<String, String>> searchInfoList(Map<String, String> result);
    public String showInfoText(String uuid);
}
