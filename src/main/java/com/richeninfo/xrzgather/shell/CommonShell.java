package com.richeninfo.xrzgather.shell;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.xrzgather.kernel.NetConfig;
import com.richeninfo.xrzgather.utils.MultiPageBean;

/**
 * <pre>
 * 如果url中有开始日期，请求前会做替换。但不能有结束日期。
 * 循环实现分页，适用于大部分网站。
 * </pre>
 * @author mz
 * @date 2013-8-15 10:37:31
 */
public class CommonShell extends Shell{

    public CommonShell() {
        this.operation = new DefaultHttpOperation();
    }

    @Override
    public void loop(NetConfig netConfig) {
        if(isMultiPage()){
            MultiPageBean multiPageBean = MultiPageBean.build(beforePage, afterPage, multiPageSelector);
            while(true){
                if(Integer.parseInt(getCurrentPageNum()) > 500){//假定每次最大只能采集500页。避免死循环
                    logger.warn("已经到" + getCurrentPageNum() + "页了，不会再给你采了！");
                    break;
                }
                listPageUrl = multiPageBean.nextPageUrl(listPageUrl, Integer.parseInt(getCurrentPageNum()), listPageHtml);
                setCurrentPageNum(Integer.parseInt(getCurrentPageNum()) + 1 + "");
                logger.debug("下页链接：" + listPageUrl);
                beforeRequest(getEndDate());
                listPageHtml = requestListPage();
                gather(netConfig);
            }
        }
    }
}
