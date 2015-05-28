package com.richeninfo.xrzgather.kernel.resulthandler;

import com.richeninfo.xrzgather.ApplicationHelper;
import com.richeninfo.xrzgather.dao.GatherDao;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author mz
 * @date 2013-8-13 15:16:03
 */
public class SqlExecuteHandler extends ResultHandler{
    private String daoName;
    private String sqlId;

    @Override
    public void handle(Map<String, Object> currResult) {
        DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) ApplicationHelper.getInstance().getBean("sqlSessionFactory");
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Class<? extends GatherDao> clazz = (Class<? extends GatherDao>) Class.forName(GatherDao.class.getPackage().getName() + "." + daoName);
            Method method = BeanUtils.findDeclaredMethod(clazz, sqlId, new Class[]{Map.class});
            Object result = method.invoke(session.getMapper(clazz), currResult);
            currResult.put(key, result);
            session.commit();
        } catch (Exception ex) {
            logger.warn("执行sql出现异常。daoName:" + daoName + ",sqlId:" + sqlId, ex);
        } finally{
             session.close();
        }
    }

    public String getDaoName() {
        return daoName;
    }

    public void setDaoName(String daoName) {
        this.daoName = daoName;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

}
