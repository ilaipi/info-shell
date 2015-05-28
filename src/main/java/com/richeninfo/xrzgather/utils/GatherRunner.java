package com.richeninfo.xrzgather.utils;

import com.richeninfo.xrzgather.ApplicationHelper;
import com.richeninfo.xrzgather.dao.GatherFlowDao;
import com.richeninfo.xrzgather.hbean.GatherFlow;
import com.richeninfo.xrzgather.kernel.Info;
import com.richeninfo.xrzgather.kernel.NetConfig;
import com.richeninfo.xrzgather.kernel.exceptions.StopException;
import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import com.richeninfo.xrzgather.kernel.resulthandler.ResultHandler;
import com.richeninfo.xrzgather.shell.Shell;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author mz
 * @date 2013-8-22 15:21:27
 */
public class GatherRunner {

    private static Log logger = LogFactory.getLog(GatherRunner.class);

    public static void run(String flowUUID) {
        GatherFlowDao gatherFlowDao = (GatherFlowDao) ApplicationHelper.getInstance().getBean("gatherFlowDao");
        GatherFlow flow = gatherFlowDao.getFlowInfo(flowUUID);
        if (flow == null) {
            flow = new GatherFlow();
        }
        String flowConfig = gatherFlowDao.getFlowDesc(flowUUID);
        Elements firstLevelList = FlowConfigUtil.firstLevelList(flowConfig);
        Element propertiesBox = FlowConfigUtil.getPropertiesBox(flowConfig);
        Shell shell = FlowConfigUtil.getShellConfig(flowConfig);
        List<ResultHandler> resultHandlers = null;
        try {
            resultHandlers = FlowConfigUtil.getResultHandlers(shell, firstLevelList, propertiesBox);
        } catch (Exception e) {
            logger.warn("包含不支持的类型！", e);
        }

        shell.setResultHandler(resultHandlers);
        NetConfig netConfig = new NetConfig();
        ListPage listPage = null;
        List<Field> fields = new ArrayList<>();
        try {
            listPage = FlowConfigUtil.getListPage(firstLevelList, propertiesBox);
            fields.addAll(listPage.getGlobalFields());
            fields.addAll(listPage.getFields());
        } catch (ClassNotFoundException ex) {
            logger.warn("包含不支持的类型！", ex);
        }
        netConfig.setListPage(listPage);
        if (firstLevelList.size() > 2) {
            List<Page> pages = new ArrayList<Page>();
            for (int i = 2; i < firstLevelList.size(); i++) {//解析其它页面
                Element pageConfig = firstLevelList.get(i);
                Page page = null;
                try {
                    page = FlowConfigUtil.getPage(pageConfig, propertiesBox);
                    fields.addAll(page.getFields());
                } catch (ClassNotFoundException ex) {
                    logger.warn("包含不支持的类型！", ex);
                }
                pages.add(page);
            }
            netConfig.setPages(pages);
        }
        try {
            shell.setHeaders(FlowConfigUtil.buildMap(flow.getRequestHeaders(), "\n", ":"));
            shell.setNameAndValues(FlowConfigUtil.buildMap(flow.getRequestBodys(), "&", "="));
            logger.info("Shell实例：" + shell.getClass().getName() + "开始执行");
            if (CollectionUtils.isNotEmpty(fields)) {
                for (Field field : fields) {
                    if (Info.INFO_TITLE.name().equals(field.getName())) {
                        netConfig.setHasInfoNameField(Boolean.TRUE);
                    }
                }
            }
            String message = "flow uuid[" + flowUUID + "采集";
            try {
                shell.execute(netConfig);
                logger.info(message + "成功完成");
            } catch (StopException e) {
                if (StringUtils.isNotBlank(shell.getBaseGatherInfo())) {
                    message += (shell.getBaseGatherInfo() + " 停止。停止原因：" + e.getMessage());
                } else {
                    message += "结束";
                }
                logger.warn(message);
            }
        } catch (Exception e) {
            if (StringUtils.isNotBlank(shell.getBaseGatherInfo())) {
                logger.warn(shell.getBaseGatherInfo(), e);
            } else {
                logger.warn("what's wrong?", e);
            }
        }
    }
}
