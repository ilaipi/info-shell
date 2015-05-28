package com.richeninfo.xrzgather.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by yyam on 15-2-27.
 */
public interface GatherHotelDao {
    public void saveGatherResult(Map<String, Object> result);
    public List<Map<String, String>> searchInfoList(Map<String, String> result);
}
