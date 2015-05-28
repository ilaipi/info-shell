package com.richeninfo.xrzgather.utils;

import com.richeninfo.xrzgather.kernel.field.Field;
import com.richeninfo.xrzgather.kernel.field.GlobalField;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.FieldValue;
import com.richeninfo.xrzgather.kernel.field.handler.FieldValueHandler;
import com.richeninfo.xrzgather.kernel.locate.Locate;
import com.richeninfo.xrzgather.kernel.page.ListPage;
import com.richeninfo.xrzgather.kernel.page.Page;
import com.richeninfo.xrzgather.kernel.resulthandler.AttachmentFromInfoTextHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.AttachmentFromListTag;
import com.richeninfo.xrzgather.kernel.resulthandler.ExistsHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.ResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.SaveResultHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.SuitableTitleHandler;
import com.richeninfo.xrzgather.shell.Shell;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author mz
 * @date 2013-8-12 10:28:51
 */
public class FlowConfigUtil {
    /**
     * html中，流程配置div的id
     */
    public static final String FLOW_CONFIG_ID = "tools";
    /**
     * html中，流程配置div中，第一级列表的ul的id
     */
    public static final String FIRST_LEVEL_LIST = "first-level-list";
    /**
     * html中，所有组件的属性div的id
     */
    public static final String PROPERTIES_ID = "properties";
    
    /**
     * 每个组件的li中，标记组件的属性盒子id的span的选择器
     */
    public static final String TOOL_ID_SELECTOR = "span.tool_id";
    
    /**
     * 每个组件的li中，标记组件的java中类型的span的选择器
     */
    public static final String JAVA_TYPE_SELECTOR = "span.java-type";
    
    /**
     * 每个组件的li中，标记组件的界面中类型的span的选择器
     */
    public static final String TOOL_TYPE_SELECTOR = "span.x-type";
    
    /**
     * 列表页面的属性盒子的id
     */
    public static final String LIST_PAGE_PROPERTIES_BOX_ID = "list-page-properties-box";
    
    private static Log logger = LogFactory.getLog(FlowConfigUtil.class);
    public static Shell getShellConfig(String flowConfig){
        Elements firstLevelList = firstLevelList(flowConfig);
        Element shellConfig = getShellConfig(firstLevelList);
        String javaType = getToolJavaType(shellConfig);
        try {
            Element propertieConfig = getPropertiesBox(flowConfig);//得到所有工具的属性的配置
            Class<Shell> shellClass = (Class<Shell>) getToolClass(Shell.class, javaType);
            Shell shell = setProperties(shellClass, getElementPropertiesBox(propertieConfig, getToolId(shellConfig)));
            return shell;
        } catch (ClassNotFoundException ex) {
            logger.warn("不支持的壳：" + javaType, ex);
        }
        return null;
    }
    
    public static ListPage getListPage(Elements firstLevelList, Element propertiesBox) throws ClassNotFoundException{
        Element listPageConfig = firstLevelList.get(1);//第二个一定是列表页面
        ListPage listPage = setProperties(ListPage.class, getElementPropertiesBox(propertiesBox, LIST_PAGE_PROPERTIES_BOX_ID));
        //找全局字段
        Elements globalFieldConfigs = getFieldsConfig(listPageConfig, "global-fields", "x-type-global-field");
        List<Field> globalFields = new ArrayList<Field>();
        for(int i = 0;i<globalFieldConfigs.size();i++){
            Element globalFieldConfig = globalFieldConfigs.get(i).parent();
            String javaType = getToolJavaType(globalFieldConfig);
            String toolId = getToolId(globalFieldConfig);
            Class<GlobalField> fieldClass = (Class<GlobalField>) getToolClass(Field.class, javaType);
            GlobalField field = setProperties(fieldClass, getElementPropertiesBox(propertiesBox, toolId));
            buildField(field, globalFieldConfig, propertiesBox);
            globalFields.add(field);
            logger.debug("---add global field:" + field.getDisplayName() + "    " + field.getName());
        }
        Elements listFieldConfigs = getFieldsConfig(listPageConfig, "list-fields", "x-type-list-field");
        List<Field> fields = new ArrayList<Field>();
        for(int i = 0;i<listFieldConfigs.size();i++){
            Element listFieldConfig = listFieldConfigs.get(i).parent();
            String javaType = getToolJavaType(listFieldConfig);
            String toolId = getToolId(listFieldConfig);
            Class<Field> fieldClass = (Class<Field>) getToolClass(Field.class, javaType);
            Field field = setProperties(fieldClass, getElementPropertiesBox(propertiesBox, toolId));
            buildField(field, listFieldConfig, propertiesBox);
            fields.add(field);
            logger.debug("---add list field:" + field.getDisplayName() + "    " + field.getName());
        }
        listPage.setGlobalFields(globalFields);
        listPage.setFields(fields);
        //找列表字段
        return listPage;
    }
    
    public static Page getPage(Element pageConfig, Element propertiesBox) throws ClassNotFoundException{
        Elements locateConfigs = pageConfig.getElementsContainingOwnText("x-type-page-locate");
        Page page = setProperties(Page.class, getElementPropertiesBox(propertiesBox, getToolId(pageConfig)));
        if(locateConfigs.size() > 0){
            Element locateConfig = locateConfigs.get(0).parent();
            String locateJavaType = getToolJavaType(locateConfig);
            Class<Locate> locateClazz = (Class<Locate>) getToolClass(Locate.class, locateJavaType);
            Locate locate = setProperties(locateClazz, getElementPropertiesBox(propertiesBox, getToolId(locateConfig)));
            page.setLocate(locate);
        }
        Elements listFieldConfigs = getFieldsConfig(pageConfig, "", "x-type-list-field");
        List<Field> fields = new ArrayList<Field>();
        for(int i = 0;i<listFieldConfigs.size();i++){
            Element listFieldConfig = listFieldConfigs.get(i).parent();
            String javaType = getToolJavaType(listFieldConfig);
            String toolId = getToolId(listFieldConfig);
            Class<Field> fieldClass = (Class<Field>) getToolClass(Field.class, javaType);
            Field field = setProperties(fieldClass, getElementPropertiesBox(propertiesBox, toolId));
            buildField(field, listFieldConfig, propertiesBox);
            logger.debug("---add list field:" + field.getDisplayName() + "    " + field.getName());
            fields.add(field);
        }
        page.setFields(fields);
        return page;
    }
    
    private static void buildField(Field field, Element fieldConfig, Element propertiesBox) throws ClassNotFoundException{
        //解析字段的定位器
        Elements locateConfigs = fieldConfig.getElementsContainingOwnText("x-type-locate");
        if(locateConfigs.size() > 0){
            Element locateConfig = locateConfigs.get(0).parent();
            String locateJavaType = getToolJavaType(locateConfig);
            Class<Locate> locateClazz = (Class<Locate>) getToolClass(Locate.class, locateJavaType);
            Locate locate = setProperties(locateClazz, getElementPropertiesBox(propertiesBox, getToolId(locateConfig)));
            field.setLocate(locate);
        }
        //解析字段的字段值
        Elements fieldValueConfigs = fieldConfig.getElementsContainingOwnText("x-type-fieldvalue");
        if(fieldValueConfigs.size() > 0){
            Element fieldValueConfig = fieldValueConfigs.get(0).parent();
            String fieldValueJavaType = getToolJavaType(fieldValueConfig);
            Class<FieldValue> fieldValueClazz = (Class<FieldValue>) getToolClass(FieldValue.class, fieldValueJavaType);
            FieldValue fieldValue = setProperties(fieldValueClazz, getElementPropertiesBox(propertiesBox, getToolId(fieldValueConfig)));
            field.setFieldValue(fieldValue);
        }
        //解析字段的字段处理器
        Elements fieldValueHandlerConfigs = fieldConfig.getElementsContainingOwnText("x-type-fieldhandler");
        if(fieldValueHandlerConfigs.size() > 0){
            Element fieldVlaueHandlerConfig = fieldValueHandlerConfigs.get(0).parent();
            String fieldVlaueHandlerJavaType = getToolJavaType(fieldVlaueHandlerConfig);
            Class<FieldValueHandler> fieldValueHandlerClazz = (Class<FieldValueHandler>) getToolClass(FieldValueHandler.class, fieldVlaueHandlerJavaType);
            FieldValueHandler fieldValueHandler = setProperties(fieldValueHandlerClazz, getElementPropertiesBox(propertiesBox, getToolId(fieldVlaueHandlerConfig)));
            field.setHandler(fieldValueHandler);
        }
    }
    
    private static Elements getFieldsConfig(Element listPageConfig, String divId, String xType){
        if(StringUtils.isBlank(divId)){
            return listPageConfig.getElementsContainingOwnText(xType);
        }
        Element globalFieldList = listPageConfig.getElementById(divId);
        //globalFieldList = Jsoup.parse(globalFieldList.html());
        Elements globalFieldConfigs = globalFieldList.getElementsContainingOwnText(xType);
        return globalFieldConfigs;
    }
    
    /**
     * 从第一级列表中解析到shell的配置。
     * shell的配置必须是第一个li。
     * 如果不是将报错。
     * @param firstLevelList
     * @return 
     */
    public static Element getShellConfig(Elements firstLevelList){
        Element shellConfig = firstLevelList.first();
        return shellConfig;
    }
    
    public static Class<?> getToolClass(Class<?> parent, String javaType) throws ClassNotFoundException{
        Class<?> clazz = Class.forName(parent.getPackage().getName() + "." + javaType);
        return clazz;
    }
    
    /**
     * 得到壳下配置的所有结果处理器
     * @param flowConfig
     * @return
     * @throws ClassNotFoundException 
     */
    public static List<ResultHandler> getResultHandlers(Shell shell, Elements firstLevelList, Element propertiesBox) throws ClassNotFoundException{
        Element shellConfig = getShellConfig(firstLevelList);
        Elements resultHandlerConfigs = shellConfig.select("ul>li");
        if(resultHandlerConfigs.size() > 0){
            List<ResultHandler> resultHandlers = new ArrayList<ResultHandler>();
            for(int i = 0;i<resultHandlerConfigs.size();i++){
                Element resultHandlerConfig = resultHandlerConfigs.get(i);
                String toolJavaType = getToolJavaType(resultHandlerConfig);
                String toolId = getToolId(resultHandlerConfig);
                Class<ResultHandler> clazz = (Class<ResultHandler>) Class.forName(ResultHandler.class.getPackage().getName() + "." + toolJavaType);
                ResultHandler resultHandler = setProperties(clazz, getElementPropertiesBox(propertiesBox, toolId));
                if(resultHandler instanceof ExistsHandler){
                    shell.setExistsHandler(resultHandler);
                    continue;
                }
                if(resultHandler instanceof SaveResultHandler){
                    shell.setSaveHandler(resultHandler);
                    continue;
                }
                if(resultHandler instanceof SuitableTitleHandler){
                    shell.setSuitableTitleHandler(resultHandler);
                    continue;
                }
                if(resultHandler instanceof AttachmentFromListTag){
                    shell.setListTagAttachment((AttachmentFromListTag) resultHandler);
                    continue;
                }
                if(resultHandler instanceof AttachmentFromInfoTextHandler){
                    shell.setSaveAttachmentHandler(resultHandler);
                    continue;
                }
                resultHandlers.add(resultHandler);
            }
            return resultHandlers;
        }
        return null;
    }
    
    /**
     * 创建工具所属的java对象，并且找到其属性盒子，把所有的属性的值赋值到对应的属性
     * @param <K>
     * @param clazz
     * @param propertiesBox 工具的属性盒子的div
     * @param toolConfig 要生成的对象的html配置。包括属性盒子id、javatype、x-type、tool_name。在这里有用的只有属性盒子id
     * @return 
     */
    private static <K> K setProperties(Class<?> clazz, Element propertiesBox){
        try {
            K k = (K) clazz.newInstance();
            if(propertiesBox == null){
                return k;
            }
            //得到所有有name属性的input
            Elements inputs = propertiesBox.select("input[name]");
            ConvertUtils.register(new BeanUtilsDateConvert(), Date.class);
            for(Element input : inputs){
                String nameAttr = input.attr("name");
                String value = input.val();
                try {
                    logger.debug("set properties: " + clazz.getName() + "[" + nameAttr + "    " + value + "]");
                    BeanUtils.setProperty(k, nameAttr, value);
                } catch (Exception ex) {
                    logger.warn("设置属性失败。name:" + nameAttr + ", value:" + value, ex);
                }
            }
            return k;
        } catch (InstantiationException ex) {
            logger.warn("属性设置异常。不能实例化对象：" + clazz.getName(), ex);
        } catch (IllegalAccessException ex) {
            logger.warn("属性设置异常。类" + clazz.getName() + "不能访问", ex);
        }
        return null;
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        String flowConfig = FileUtils.readFileToString(new File("C:\\Users\\mz\\Desktop\\采集\\flowConfig.txt"));
//        Elements firstLevelList = firstLevelList(flowConfig);
//        Element propertiesBox = getPropertiesBox(flowConfig);
//        Shell shell = getShellConfig(flowConfig);
//        List<ResultHandler> resultHandlers = getResultHandlers(firstLevelList, propertiesBox);
//        shell.setResultHandler(resultHandlers);
//        NetConfig netConfig = new NetConfig();
//        ListPage listPage = getListPage(firstLevelList, propertiesBox);
//        netConfig.setListPage(listPage);
//        if(firstLevelList.size() > 2){
//            List<Page> pages = new ArrayList<Page>();
//            for(int i = 2;i<firstLevelList.size();i++){//解析其它页面
//                Element pageConfig = firstLevelList.get(i);
//                Page page = getPage(pageConfig, propertiesBox);
//                pages.add(page);
//            }
//            netConfig.setPages(pages);
//        }
//        shell.execute(netConfig);
        System.out.println(Calendar.MONTH);
    }
    
    public static Element getElementPropertiesBox(Element propertieConfig, String toolId){
        if(StringUtils.isBlank(toolId)){
            return null;
        }
        Element shellPropertiesBox = propertieConfig.getElementById(toolId);
        return shellPropertiesBox;
    }
    
    public static String getToolId(Element toolConfig){
        Elements toolIdSpans = toolConfig.select(TOOL_ID_SELECTOR);
        if(toolIdSpans != null && toolIdSpans.size() > 0){
            return toolIdSpans.get(0).ownText();
        }
        return "";
    }
    
    public static String getToolJavaType(Element toolConfig){
        Elements toolIdSpans = toolConfig.select(JAVA_TYPE_SELECTOR);
        if(toolIdSpans != null && toolIdSpans.size() > 0){
            return toolIdSpans.get(0).ownText();
        }
        return "";
    }
    
    public static Elements firstLevelList(String flowConfig){
        Document doc = Jsoup.parse(flowConfig);
        Element tools = doc.getElementById(FLOW_CONFIG_ID);//得到工具的配置
        Element firstLevel = tools.getElementById(FIRST_LEVEL_LIST);//得到一级列表对象。包含：壳、列表页面、页面
        Elements firstLevelList = firstLevel.children();//一级列表
        return firstLevelList;
    }
    
    public static Element getPropertiesBox(String flowConfig){
        Document doc = Jsoup.parse(flowConfig);
        Element propertieConfig = doc.getElementById(PROPERTIES_ID);//得到所有工具的属性的配置
        return propertieConfig;
    }
    
    public static String getToolType(Element toolConfig){
        Elements toolIdSpans = toolConfig.select(TOOL_TYPE_SELECTOR);
        if(toolIdSpans != null && toolIdSpans.size() > 0){
            return toolIdSpans.get(0).ownText();
        }
        return "";
    }
    
    /**
     * 把 requestHeader 首先按照 listSeparator拆分为数组。
     * 数组中的每个元素按照seperator进行两边拆，得到左边为key，右边为value
     * @param requestHeader
     * @param listSeparator
     * @param seperator
     * @return 
     */
    public static Map<String, String> buildMap(String requestHeader, String listSeparator, String seperator){
        if(StringUtils.isBlank(requestHeader)){
            return null;
        }
        String[] headers = StringUtils.split(requestHeader, listSeparator);
        Map<String, String> headerMap = new HashMap<String, String>();
        for(String header : headers){
            headerMap.put(StringUtils.substringBefore(header, seperator).trim(), StringUtils.substringAfter(header, seperator).trim());
        }
        return headerMap;
    }
}
