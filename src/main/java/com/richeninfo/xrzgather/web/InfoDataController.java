package com.richeninfo.xrzgather.web;

import com.richeninfo.xrzgather.ApplicationHelper;
import com.richeninfo.xrzgather.dao.GatherDao;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * 
 * @author mz
 * @date 2013-8-26 10:11:56
 */
public class InfoDataController extends MultiActionController{
    public ModelAndView searchInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String daoName = request.getParameter("daoName");
        String queryStr = request.getParameter("queryStr");
        String infoId = request.getParameter("infoId");
        DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) ApplicationHelper.getInstance().getBean("sqlSessionFactory");
        SqlSession session = sqlSessionFactory.openSession();
        String sqlId = "searchInfoList";
        try {
            Map<String, String> param = new HashMap<>();
            if(StringUtils.isNotBlank(queryStr)){
                queryStr = "%" + queryStr + "%";
            } else{
                queryStr = "";
            }
            param.put("title", queryStr);
            param.put("uuid", queryStr);
            param.put("INFO_SOURCE", queryStr);
            param.put("infoId", infoId);
            Class<? extends GatherDao> clazz = (Class<? extends GatherDao>) Class.forName(GatherDao.class.getPackage().getName() + "." + daoName);
            Method method = BeanUtils.findDeclaredMethod(clazz, sqlId, new Class[]{Map.class});
            Object result = method.invoke(session.getMapper(clazz), param);
            if(result != null){
                List<Map<String, String>> data = (List<Map<String, String>>) result;
                ModelAndView modelAndView = new ModelAndView("jsonView");
                modelAndView.addObject("data", data);
                return modelAndView;
            }
        } catch (Exception ex) {
            logger.warn("执行sql出现异常。daoName:" + daoName + ",sqlId:" + sqlId, ex);
        } finally{
             session.close();
        }
        return null;
    }
    public ModelAndView showInfoText(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String daoName = request.getParameter("daoName");
        String uuid = request.getParameter("uuid");
        DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) ApplicationHelper.getInstance().getBean("sqlSessionFactory");
        SqlSession session = sqlSessionFactory.openSession();
        String sqlId = "showInfoText";
        try {
            Class<? extends GatherDao> clazz = (Class<? extends GatherDao>) Class.forName(GatherDao.class.getPackage().getName() + "." + daoName);
            Method method = BeanUtils.findDeclaredMethod(clazz, sqlId, new Class[]{String.class});
            Object result = method.invoke(session.getMapper(clazz), uuid);
            if(result != null){
                ModelAndView modelAndView = new ModelAndView("flowDescView");
                modelAndView.addObject("flowDesc", result);
                return modelAndView;
            }
        } catch (Exception ex) {
            logger.warn("执行sql出现异常。daoName:" + daoName + ",sqlId:" + sqlId, ex);
        } finally{
             session.close();
        }
        return null;
    }
}
