package com.richeninfo.xrzgather.shell;

import com.richeninfo.common.util.DateUtils;
import com.richeninfo.http.HttpOperation;
import com.richeninfo.http.utils.HttpOperationUtils;
import com.richeninfo.xrzgather.kernel.Info;
import com.richeninfo.xrzgather.kernel.NetConfig;
import com.richeninfo.xrzgather.kernel.exceptions.ContinueException;
import com.richeninfo.xrzgather.kernel.exceptions.StopException;
import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.SubURL;
import com.richeninfo.xrzgather.kernel.iterator.InfoIterator;
import com.richeninfo.xrzgather.kernel.iterator.JsoupIterator;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import com.richeninfo.xrzgather.kernel.page.handler.PageHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.AttachmentFromListTag;
import com.richeninfo.xrzgather.kernel.resulthandler.ResultHandler;
import com.richeninfo.xrzgather.kernel.stopmechanism.*;
import com.richeninfo.xrzgather.utils.MultiPageBean;
import jodd.jerry.Jerry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mz
 * @date 2013-8-8 9:43:31
 */
public abstract class Shell {
    protected Log logger = LogFactory.getLog(this.getClass());
    protected String netUrl;//网站的地址。备用
    protected String listPageUrl;//列表页面的url。可以包含日期通配符
    protected String netName;//网站名称
    protected String gatherPrograma;//采集栏目
    protected String realListPageUrl;//处理过的列表页面url
    protected Map<String, String> headers;//请求头
    protected Map<String, String> nameAndValues;//请求参数
    protected Map<String, String> realNameAndValues;//请求参数
    
    private Date startDate;//给默认值为当前时间
    
    private Boolean multiPage;//是否需要分页
    private String firstPageNum;//列表页面第一页的页码(主要是因为有的网站第二页是1)。默认值为1
    private String currentPageNum;//标记当前采集的页码。当desc为false时要判断是不是第一页(currentPageNum equals firstPageNum)
    protected String beforePage;//页码前
    protected String afterPage;//页码后
    protected String multiPageSelector;//分页选择器
    
    protected String stopMechanism;
    protected StopCode stopCode;
    
    protected HttpOperation operation;
    
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    protected List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    
    protected List<ResultHandler> resultHandler;
    
    /**
     * <pre>
     * 判断资讯是否已经采集。用于：
     *      getDesc() = Boolean.FALSE，停止机制的调用
     * </pre>
     * @see #getDesc() 
     */
    protected ResultHandler existsHandler;//是否存在处理器
    /**
     * <pre>
     * 查询上次采集到的最合适做停止判断的处理器。
     * 最合适：updateTime最小，pubDate最大
     * 用于：
     *      getDesc() = Boolean.TRUE，采集开始前调用，得到的title将设置给suitableTitle
     * </pre>
     * @see #suitableTitle
     */
    protected ResultHandler suitableTitleHandler;
    /**
     * 适合用于停止遍历列表的标题。若不为空，则优先级最高，一旦equals采集标题就stop
     */
    protected String suitableTitle;
    
    protected ResultHandler saveHandler;//保存处理器
    
    protected ResultHandler saveAttachmentHandler;
    
    protected AttachmentFromListTag listTagAttachment;
    
    protected String listPageHtml;
    
    /**
     * <pre>
     * 是否有名称字段。如果没有 INFO_TITLE字段，就表示使用列表标签中的链接的ownText作为INFO_TITLE。
     * 默认值：Boolean.FALSE
     * </pre>
     * @see Info#INFO_TITLE
     */
    protected Boolean hasNameField;
    
    /**
     * <pre>
     * 标记列表标签是否按照日期倒叙排列。
     * 默认为true。
     * 如果是false，那么第一页发现重复的会continue，发现比startDate早的也会continue
     * </pre>
     */
    private Boolean desc;
    
    protected Map<String, String> fieldValueMap;//存放一次结果。每个列表标签遍历开始时重新初始化
    /**
     * 每次调用gather方法，修改此字段的值
     */
    private String baseGatherInfo;
    
    private InfoIterator iterator;
    
    /**
     * 执行是否存在的停止机制
     */
    protected void existsStopMechanism(){
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.putAll(fieldValueMap);
        if(StringUtils.contains(stopMechanism, IfExistsContinue.class.getSimpleName())){
            existsHandler.handle(tempMap);
            Integer exists = (Integer) tempMap.get(existsHandler.getKey());
            stopCode = new IfExistsContinue().stop(fieldValueMap.get(Info.INFO_TITLE.name()), exists);
        } else if(StringUtils.contains(stopMechanism, IfExistsStop.class.getSimpleName())){
            existsHandler.handle(tempMap);
            Integer exists = (Integer) tempMap.get(existsHandler.getKey());
            stopCode = new IfExistsStop().stop(fieldValueMap.get(Info.INFO_TITLE.name()), exists);
        } else if(StringUtils.contains(stopMechanism, RepeatRemarkAndContinue.class.getSimpleName())){
            stopCode = new RepeatRemarkAndContinue().stop(fieldValueMap, startDate);
        }
    }
    
    private void gatherField(Field field, String html) throws ContinueException, StopException {
        String value = "";
        try{
            value = field.getValue(html);
        } catch(Exception e){
            logger.warn(baseGatherInfo + "字段：" + field.getDisplayName() + "{" + field.getName() + "}采集异常", e);
            fieldValueMap.put(field.getName(), "");
            return;
        }
        if(fieldValueMap.containsKey(field.getName()) && StringUtils.isBlank(value)){
            //当结果中已经有了这个字段的值，并且新采集到的值是空的，那么不会再替换这个值。并且也不需要做其他的判断了，直接下一个字段
            //主要是为了处理 国务院机关事务管理局 网站的 “出处”的采集。网页上有个showSource的js函数，若值不为空则使用，否则使用常量“国管局”
            //现在可以设置一个常量字段为国管局，再设置采集字段。若采集到的为空，就不会使用采集到的
            return;
        }
        fieldValueMap.put(field.getName(), value);
        if (field.getFieldValue() instanceof SubURL) {
            fieldValueMap.put(Info.INFO_URL.name(), value);
        }

        if (field.getName().equals(Info.INFO_TITLE.name())) {//如果有INFO_TITLE字段，并且当前采集的字段是INFO_TITLE字段，那么判断停止机制
            //没有配置标题字段，那么默认的标题就是列表标签中a的text。如果标题需要做处理，那么一定需要配置一个字段，即hasNameField = Boolean.TRUE
            //无论是否倒叙，都要支持设置一个标题，遇到此标题则Stop
            infoTitleStop();
        }
        if (field.getName().equals(Info.INFO_PUBDATE.name())) {
            if(StringUtils.contains(stopMechanism, OlderDateStop.class.getSimpleName())){
                stopCode = new OlderDateStop().stop(startDate, value);
                switch (stopCode) {
                    case STOP:
                        if (!getDesc() && getCurrentPageNum().equals(getFirstPageNum())) {
                        throw new ContinueException(stopCode.getReason());
                    } else {
                        throw new StopException(stopCode.getReason());
                    }
                    case CONTINUE://continue的不是字段的循环，而是列表的循环，所以必须return，由gather方法去continue
                        throw new ContinueException(stopCode.getReason());
                    case NORMAL:
                        break;
                }
            }
        }
    }
    
    private void infoTitleStop() throws ContinueException, StopException{
        if(StringUtils.isNotBlank(suitableTitle)){
            stopCode = new SuitablePreviousTitle().stop(suitableTitle, fieldValueMap.get(Info.INFO_TITLE.name()));
            switch (stopCode) {
                case STOP:
                    throw new StopException(stopCode.getReason());
                case CONTINUE:
                    throw new ContinueException(stopCode.getReason());
                case NORMAL:
                    break;
            }
            //return;
        }
        existsStopMechanism();
        switch (stopCode) {
            case STOP:
                //是第一页那么不能stop，只能continue
                if (!getDesc() && getCurrentPageNum().equals(getFirstPageNum())) {
                    throw new ContinueException();
                } else {
                    throw new StopException(stopCode.getReason());
                }
            case CONTINUE:
                throw new ContinueException(stopCode.getReason());
            case NORMAL:
                break;
        }
    }
    
    protected void gatherListTag(String tagHtml, ListPage listPage){
        Jerry j = Jerry.$(listPage.getLinkCssSelector(), Jerry.jerry(tagHtml));
        if(j.size() < 1){
            throw new IllegalArgumentException("不能从列表标签中选择到链接。链接选择器：" + listPage.getLinkCssSelector());
        } else{
            String linkOwnText = j.text().replace("&nbsp;", " ").trim();
            fieldValueMap.put(Info.INFO_URL.name(), HttpOperationUtils.getAbsoluteUrl(realListPageUrl, j.attr("href").trim()));
            fieldValueMap.put(Info.INFO_TITLE.name(), linkOwnText);
            fieldValueMap.put(Info.ORIGINAL_INFO_TITLE.name(), linkOwnText);
        }
    }
    
    protected void gatherListField(String tagHtml, Field listField) throws ContinueException{
        gatherField(listField, tagHtml);
    }
    
    /**
     * <pre>
     * 根据stopCode判断后续操作(STOP[中止采集]、CONTINUE[采集下一个列表标签]、NORMAL[继续当前操作])
     * 具体规则：
     *      1.如果遇到某一页根据列表选择器定位得到的列表集合为空，则STOP
     *      2.根据列表是否按照日期倒叙
     *          2.1 是  (getDesc() = Boolean.TRUE，默认值)
     *              2.1.1 若采集标题为空，则CONTINUE
     *              2.1.2 若采集标题 equals suitableTitle，则STOP
     *              2.1.3 其它情况则CONTINUE
     *          2.2 否  (getDesc() = Boolean.FALSE)
     *              2.2.1 若采集标题为空，则CONTINUE
     *              2.2.2 执行是否存在结果处理器，把得到的值传给对应的停止机制
     *              2.2.3 若当前采集页码不是第一页，以停止机制的结果为准
     *              2.2.4 若当前采集页码不是第一页，停止机制的STOP结果被视为CONTINUE
     * </pre>
     * @param netConfig 
     * @see #getDesc() 
     * @see #getFirstPageNum() 
     * @see #suitableTitle
     * @see #existsHandler
     */
    public void gather(NetConfig netConfig) {
        hasNameField = netConfig.getHasInfoNameField();
        ListPage listPage = netConfig.getListPage();
        //1.解析列表标签
        if(StringUtils.isBlank(listPage.getListCssSelector())){
            throw new IllegalArgumentException("列表页面的列表选择器不能为空");
        }
        listPage.setPageUrl(realListPageUrl);
        baseGatherInfo = gatherPrograma + "(" + DateUtils.format(getStartDate(), "yyyy-MM-dd") + ")  第" + (StringUtils.isBlank(currentPageNum) ? getFirstPageNum() : currentPageNum) + "页";
        //2.解析列表标签外的全局字段
        //全局字段不检查 停止机制
        Map<String, String> globalFieldValueMap = new HashMap<String, String>();
        globalFieldValueMap.put(Info.infoSource.name(), gatherPrograma);
        globalFieldValueMap.put(Info.INFO_NET_NAME.name(), netName);
        if(CollectionUtils.isNotEmpty(listPage.getGlobalFields())){
            for(Field field : listPage.getGlobalFields()){
                try{
                    String value = field.getValue(listPageHtml);
                    globalFieldValueMap.put(field.getName(), value);
                    if(field.getFieldValue() instanceof SubURL){
                        globalFieldValueMap.put(Info.INFO_URL.name(), value);
                    }
                } catch(Exception e){
                    stopCode = StopCode.STOP.setReason("全局字段：" + field.getName() + "采集异常");
                    logger.warn(stopCode.getReason(), e);
                    throw new StopException(stopCode.getReason());
                }
            }
        }
        //在创建迭代器之前，需要执行ListPage的Handler
        if(StringUtils.isNotBlank(listPage.getPageHandle())){
            try {
                PageHandler pageHandler = (PageHandler) Class.forName(PageHandler.class.getPackage().getName() + "." + listPage.getPageHandle()).newInstance();
                listPageHtml = pageHandler.handle(listPageHtml);
            } catch (Exception ex) {
                logger.warn("执行页面处理器异常", ex);
            }
        }
        iterator = iterator.build(listPageHtml, listPage.getListCssSelector());
        
        //TODO 扩展。空列表Continue
        stopCode = new EmptyListStop().stop(iterator.size());
        switch(stopCode){
            case STOP:
                throw new StopException("空的列表");
            case NORMAL:
                break;
        }
        
        //3.循环列表标签
        nextListTag://需要continue到这里
        while(iterator.hasNext()){
            if(!iterator.hasSelectLink(listPage.getLinkCssSelector())){
                iterator.goNext();
                continue;
            }
            stopCode = StopCode.NORMAL;
            fieldValueMap = new HashMap<String, String>(globalFieldValueMap);
            fieldValueMap.put(Info.INFO_UUID.name(), UUID.randomUUID().toString());//每条资讯都生成uuid。附件入库时需要使用
            String gatherInfo = baseGatherInfo + "第" + (iterator.getIndex() + 1) + "条资讯";
            //3.1 采集列表标签的a标签
            String nextListTagHtml = iterator.next();
            gatherListTag(nextListTagHtml, listPage);
            
            if(!hasNameField){
                try {
                    //没有配置标题字段，那么默认的标题就是列表标签中a的text。如果标题需要做处理，那么一定需要配置一个字段，即hasNameField = Boolean.TRUE
                    //无论是否倒叙，都要支持设置一个标题，遇到此标题则Stop
                    infoTitleStop();
                } catch (ContinueException ex) {
                    logger.debug(gatherInfo + ex.getMessage());
                    continue;
                }
            }
            //3.2 采集列表标签中其它字段
            if(CollectionUtils.isNotEmpty(listPage.getFields())){
                for (Field field : listPage.getFields()) {
                    try {
                        gatherListField(nextListTagHtml, field);
                    } catch (ContinueException ex) {
                        logger.debug(gatherInfo + ex.getMessage());
                        continue nextListTag;
                    }
                }
            }
            //判断是否是附件，直接下载
            boolean isAttachment = false;
            if(listTagAttachment != null){
                if(listTagAttachment.isAttachment(fieldValueMap)){
                    isAttachment = true;
                    Map<String, Object> saveListTagAttach = new HashMap<>();
                    saveListTagAttach.putAll(fieldValueMap);
                    listTagAttachment.setOperation(operation);
                    listTagAttachment.handle(saveListTagAttach);
                }
            }
            
            //4.采集子页。按照配置的页面顺序进行采集
            if(CollectionUtils.isNotEmpty(netConfig.getPages()) && !isAttachment){
                for(Page subPage : netConfig.getPages()){
                    String subPageUrl = fieldValueMap.get(Info.INFO_URL.name());
                    subPage.setPageUrl(subPageUrl);
                    String subPageHtml = operation.get(subPageUrl, null, null);
                    if(subPage.getLocate() != null){
                        try{
                            subPageHtml = subPage.getLocate().locate(subPageHtml);
                        } catch(Exception e){
                            logger.warn("页面定位出现异常，将使用页面全部内容", e);
                        }
                    }
                    subPage.setPageHtml(subPageHtml);
                    for (Field field : subPage.getFields()) {
                        try {
                            gatherField(field, subPageHtml);
                        } catch (ContinueException ex) {
                            logger.debug(gatherInfo + ex.getMessage());
                            continue nextListTag;
                        }
                    }
                    if(subPage.getMultiPage()){//处理子页面的分页。目前只支持css选择器分页
                        MultiPageBean multiPageBean = MultiPageBean.build(subPage.getCssSelector());
                        StringBuffer buffer = new StringBuffer(fieldValueMap.get(Info.INFO_TEXT.name()));
                        subPage.setCurrPageNum(subPage.getFirstPageNum());
                        while(true){
                            if(subPage.getCurrPageNum() > 100){
                                logger.warn("数据页面最大支持100页");
                                break;
                            }
                            try{
                                String subPageNextUrl = multiPageBean.nextPageUrl(subPageUrl, subPage.getCurrPageNum(), subPageHtml);
                                if(StringUtils.isNotBlank(subPageNextUrl)){
                                    subPageHtml = operation.get(subPageNextUrl, null, null);
                                    for(Field field : subPage.getFields()){
                                        if(field.getName().equalsIgnoreCase(Info.INFO_TEXT.name())){
                                            try{
                                                gatherField(field, subPageHtml);
                                                buffer.append(fieldValueMap.get(Info.INFO_TEXT.name()));
                                            } catch(ContinueException e){
                                                logger.debug(gatherInfo + e.getMessage());
                                                //continue;
                                            }
                                        }
                                    }
                                }
                                subPage.setCurrPageNum(subPage.getCurrPageNum() + 1);
                            } catch(IllegalArgumentException e){
                                logger.debug(e);
                                logger.warn("翻到第" + subPage.getCurrPageNum() + "页，找不到下页链接了");
                                break;
                            }
                        }
                        fieldValueMap.put(Info.INFO_TEXT.name(), buffer.toString());
                    }
                }
            }

            //7.执行结果处理器
            Map<String, Object> handleResult = new HashMap<String, Object>(fieldValueMap);
            if(CollectionUtils.isNotEmpty(resultHandler)){
                for(ResultHandler handler : resultHandler){
                    try{
                        handler.handle(handleResult);
                    } catch(Exception e){
                        logger.warn(gatherInfo + handler.getKey() + "字段的结果处理器执行失败", e);
                    }
                }
            } 
            //记录采集结果
            StringBuffer gatherResult = new StringBuffer(gatherInfo + "采集结果：");
            for(String key : handleResult.keySet()){
                if(Info.INFO_TEXT.name().equals(key)){
                    continue;
                }
                gatherResult.append("\n\r{").append(key).append("}--{").append(handleResult.get(key)).append("}");
            }
            logger.info(gatherResult.toString());
            
            //入库之前先保存附件，会对正文中的附件链接的href做处理
            if(saveAttachmentHandler != null && !isAttachment){
                saveAttachmentHandler.setOperation(operation);
                saveAttachmentHandler.handle(handleResult);
            }
            String originalInfoTitle = (String) handleResult.get(Info.ORIGINAL_INFO_TITLE.name());
            if(StringUtils.isBlank(originalInfoTitle)){//有时候原始标题居然为空
                handleResult.put(Info.ORIGINAL_INFO_TITLE.name(), handleResult.get(Info.INFO_TITLE.name()));
            }
            
            //执行入库处理器
            if(saveHandler != null){
                saveHandler.handle(handleResult);
            }
        }
    }
    
    public abstract void loop(NetConfig netConfig);
    
    public void execute(NetConfig netConfig){
        Date endDate = getEndDate();
        beforeRequest(endDate);
        if(StringUtils.isBlank(suitableTitle) && suitableTitleHandler != null){//如果设置了合适标题处理器，并且没有设置合适标题，那么执行合适标题处理器
            Map<String, Object> suitableTitleMap = new HashMap<>();
            suitableTitleMap.put(Info.infoSource.name(), this.gatherPrograma);
            suitableTitleMap.put(Info.INFO_NET_NAME.name(), this.netName);
            suitableTitleHandler.handle(suitableTitleMap);
            suitableTitle = (String) suitableTitleMap.get(suitableTitleHandler.getKey());
        }
        this.listPageHtml = requestListPage();
        if(iterator == null){//默认使用Jsoup实现的迭代器。json类的壳在创建时一定会设置迭代器为json实现的迭代器
            iterator = new JsoupIterator();
        }
        //采集首页
        gather(netConfig);
        loop(netConfig);
    }

    public String getBaseGatherInfo() {
        return baseGatherInfo;
    }
    
    protected Date getEndDate(){
        return null;
    }
    /**
     * <pre>
     * 每次请求前都要调用此方法
     * 此方法完成的操作：
     * 1. 当前页码+1
     * 2. 处理链接和请求体中的通配符。包括开始日期、结束日期、页码
     * 3. 因为最先把当前页码+1，所以页码是下一页的数字
     * </pre>
     * @param endDate 
     */
    protected void beforeRequest(Date endDate){
        this.realListPageUrl = evaluate(endDate, listPageUrl);
        evaluateNameValueMap(endDate);
    }
    
    private void evaluateNameValueMap(Date date){
        Map<String, String> resultMap = new HashMap<String, String>();
        if(MapUtils.isNotEmpty(nameAndValues)){
            for(String key : nameAndValues.keySet()){
                String value = nameAndValues.get(key);
                value = evaluate(date, value);
                resultMap.put(key, value);
            }
        }
        this.realNameAndValues = resultMap;
    }
    
    /**
     * $operator{expression}
     * operator = date, expression = format
     * operator = startDate, expression = format
     * operator = endDate, expression = format
     * @return 
     */
    private String evaluate(Date endDate, String value){
        if(StringUtils.isBlank(value)){
            return value;
        }
        Pattern pattern = Pattern.compile("\\$([^\\{]*)(\\{([^\\}]*)\\})?");
        Matcher matcher = pattern.matcher(value);
        while(matcher.find()){
            String operator = matcher.group(1);
            String expression = matcher.group(3);
            String realValue = "";
            switch (operator) {
                case "startDate":
                    {
                        realValue = DateUtils.format(getStartDate(), expression);
                        break;
                    }
                case "endDate":
                    {
                        if(endDate == null){
                            throw new IllegalArgumentException("需要设置日期增量");
                        }
                        realValue = DateUtils.format(endDate, expression);
                        break;
                    }
                case "date":
                    realValue = DateUtils.format(getStartDate(), expression);
                    break;
                case "page":
                    realValue = getCurrentPageNum();
                    break;
            }
            value = value.replace(matcher.group(), realValue);
        }
        return value;
    }
    
    public String requestListPage(){
        if(MapUtils.isEmpty(nameAndValues)){
            return operation.get(realListPageUrl, headers, realNameAndValues).replace("&nbsp;", " ");
        } else{
            return operation.post(realListPageUrl, headers, realNameAndValues).replace("&nbsp;", " ");
        }
    }

    public InfoIterator getIterator() {
        return iterator;
    }

    public void setIterator(InfoIterator iterator) {
        this.iterator = iterator;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

    public StopCode getStopCode() {
        return stopCode;
    }

    public void setStopCode(StopCode stopCode) {
        this.stopCode = stopCode;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getNetName() {
        return netName;
    }

    public ResultHandler getExistsHandler() {
        return existsHandler;
    }

    public void setExistsHandler(ResultHandler existsHandler) {
        this.existsHandler = existsHandler;
    }

    public ResultHandler getSaveHandler() {
        return saveHandler;
    }

    public void setSaveHandler(ResultHandler saveHandler) {
        this.saveHandler = saveHandler;
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

    public String getRealListPageUrl() {
        return realListPageUrl;
    }

    public void setRealListPageUrl(String realListPageUrl) {
        this.realListPageUrl = realListPageUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getNameAndValues() {
        return nameAndValues;
    }

    public String getSuitableTitle() {
        return suitableTitle;
    }

    public void setSuitableTitle(String suitableTitle) {
        this.suitableTitle = suitableTitle;
    }

    public ResultHandler getSuitableTitleHandler() {
        return suitableTitleHandler;
    }

    public void setSuitableTitleHandler(ResultHandler suitableTitleHandler) {
        this.suitableTitleHandler = suitableTitleHandler;
    }

    public ResultHandler getSaveAttachmentHandler() {
        return saveAttachmentHandler;
    }

    public void setSaveAttachmentHandler(ResultHandler saveAttachmentHandler) {
        this.saveAttachmentHandler = saveAttachmentHandler;
    }

    public AttachmentFromListTag getListTagAttachment() {
        return listTagAttachment;
    }

    public void setListTagAttachment(AttachmentFromListTag listTagAttachment) {
        this.listTagAttachment = listTagAttachment;
    }

    public void setNameAndValues(Map<String, String> nameAndValues) {
        this.nameAndValues = nameAndValues;
    }

    public Boolean isMultiPage() {
        if(multiPage == null){
            multiPage = Boolean.TRUE;
        }
        return multiPage;
    }

    public void setMultiPage(Boolean multiPage) {
        this.multiPage = multiPage;
    }

    public HttpOperation getOperation() {
        return operation;
    }

    public void setOperation(HttpOperation operation) {
        this.operation = operation;
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public List<ResultHandler> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(List<ResultHandler> resultHandler) {
        this.resultHandler = resultHandler;
    }

    public String getBeforePage() {
        return beforePage;
    }

    public void setBeforePage(String beforePage) {
        this.beforePage = beforePage;
    }

    public String getAfterPage() {
        return afterPage;
    }

    public void setAfterPage(String afterPage) {
        this.afterPage = afterPage;
    }

    public String getMultiPageSelector() {
        return multiPageSelector;
    }

    public void setMultiPageSelector(String multiPageSelector) {
        this.multiPageSelector = multiPageSelector;
    }

    public Date getStartDate() {
        if(this.startDate == null){
            startDate = new Date();
        }
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStopMechanism() {
        return stopMechanism;
    }

    public void setStopMechanism(String stopMechanism) {
        this.stopMechanism = stopMechanism;
    }

    public String getFirstPageNum() {
        if(StringUtils.isBlank(firstPageNum)){
            firstPageNum = "1";
        }
        return firstPageNum;
    }

    public void setFirstPageNum(String firstPageNum) {
        this.firstPageNum = firstPageNum;
    }

    public String getCurrentPageNum() {
        if(StringUtils.isBlank(currentPageNum)){
            currentPageNum = getFirstPageNum();
        }
        return currentPageNum;
    }

    public void setCurrentPageNum(String currentPageNum) {
        this.currentPageNum = currentPageNum;
    }

    /**
     * 标记列表页面是否按照日期倒叙排列。默认值为Boolean.TRUE
     * @return 
     */
    public Boolean getDesc() {
        if(desc == null){
            desc = Boolean.TRUE;
        }
        return desc;
    }

    public void setDesc(Boolean desc) {
        this.desc = desc;
    }
}
