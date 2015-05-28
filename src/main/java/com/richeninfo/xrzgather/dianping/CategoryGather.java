package com.richeninfo.xrzgather.dianping;

import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import jodd.jerry.Jerry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.ho.yaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yyam on 15-3-17.
 */
public class CategoryGather {
    protected Jerry jerry;
    protected String pageHtml;

    protected String mainCategory;
    protected String[] baseInfo;
    protected String yamlFilename;

    protected Map<String, String> globalParam;

    protected Map<String, String> fieldNameMap;

    protected List<String> fieldNames;

    public static void main(String[] args) throws FileNotFoundException {
        CategoryGather gather = new CategoryGather();
        gather.mainCategory = "美食";
        gather.baseInfo = new String[]{"北京", "西堤牛排", "五彩城", "http://www.dianping.com/shop/18210520", "北京餐厅", "海淀区", "清河", "西餐"};
        gather.yamlFilename = "meishi.yaml";
        HttpOperation operation = new DefaultHttpOperation();
        gather.pageHtml = operation.get(gather.baseInfo[3], null, null);
        gather.gather();
    }

    protected Map<String, String> gather() throws FileNotFoundException {
        Map<String, Object> config = (Map<String, Object>) Yaml.load(new File(DianPing.PATH_PREFIX + yamlFilename));
        List<Map<String, Object>> fields = (List<Map<String, Object>>) config.get("fields");
        fieldNameMap = new HashMap<>();
        fieldNames = new ArrayList<>();

        globalParam = new HashMap<>();
        globalParam.put("mainCategory", mainCategory);
        for (int i = baseInfo.length - 1; i >= 0; i--) {
            if (StringUtils.isNotBlank(baseInfo[i])) {
                globalParam.put("category", baseInfo[i]);
                break;
            }
        }
        globalParam.put("region", baseInfo[5]);

        Map<String, String> result = new HashMap<>();
        String fieldHtml = "";
        for (Map<String, Object> fieldConfig : fields) {
            fieldNames.add((String) fieldConfig.get("name"));
            fieldNameMap.put((String) fieldConfig.get("name"), (String) fieldConfig.get("text"));
            try {
                //定位
                fieldHtml = locate(fieldConfig);

                //取值
                fieldHtml = parse(fieldConfig, fieldHtml);

                //值处理
                fieldHtml = handle(fieldConfig, fieldHtml);
            } catch (Exception e) {
                System.out.println("Error------------field: [" + fieldConfig.get("name") + "] message:" + e.getMessage());
                continue;
            }

            result.put((String) fieldConfig.get("name"), fieldHtml);
            System.out.println(fieldConfig.get("name") + " : " + fieldConfig.get("text") + " : " + fieldHtml);
        }
        System.out.println("----The End----");
        return result;
    }

    private String locate(Map<String, Object> fieldConfig) {
        List<Map<String, Object>> locates = (List<Map<String, Object>>) MapUtils.getObject(fieldConfig, "locate");
        String fieldHtml = pageHtml;
        if (CollectionUtils.isNotEmpty(locates)) {
            for (Map<String, Object> locate : locates) {
                switch ((String)locate.get("type")) {
                    case "css":
                        fieldHtml = cssLocate(fieldHtml, locate);
                        break;
                    case "xpath":
                        throw new UnsupportedOperationException("不支持xpath定位");
                    case "start-end":
                        fieldHtml = startEndLocate(fieldHtml, locate);
                        break;
                }
            }
        }
        return fieldHtml;
    }

    private String parse(Map<String, Object> fieldConfig, String fieldHtml) {
        Map<String, Object> parse = (Map<String, Object>) fieldConfig.get("parse");
        String from = "text";
        if (MapUtils.isNotEmpty(parse)) {
            from = (String) parse.get("from");
        }
        String value = "";
        switch (from) {
            case "global":
                value = globalParse(parse);
                break;
            case "reg":
                value = regParse(parse, fieldHtml);
                break;
            case "text":
            default:
                value = textParse(fieldHtml);
                break;
        }
        if (StringUtils.isNotBlank(value)) {
            value = value.trim();
        }
        return value;
    }

    private String handle(Map<String, Object> fieldConfig, String value) {
        List<Map<String, Object>> handlers = (List<Map<String, Object>>) fieldConfig.get("handler");
        if (CollectionUtils.isNotEmpty(handlers)) {
            String type = "";
            for (Map<String, Object> handler : handlers) {
                type = (String) handler.get("type");
                switch (type) {
                    case "replace":
                        value = replaceHandler(handler, value);
                        break;
                }
            }
        }
        if (StringUtils.isNotBlank(value)) {
            value = value.trim();
        }
        return value;
    }

    protected String replaceHandler(Map<String, Object> handler, String value) {
        Boolean reg = MapUtils.getBoolean(handler, "reg", true);
        String target = MapUtils.getString(handler, "target");
        String replacement = MapUtils.getString(handler, "replacement", "");
        if (reg) {
            value = value.replaceAll(target, replacement);
        } else {
            value = StringUtils.replace(value, target, replacement);
        }
        return value;
    }

    protected String globalParse(Map<String, Object> parse) {
        String var = MapUtils.getString(parse, "var");
        if (globalParam.containsKey(var)) {
            return globalParam.get(var);
        }
        throw new IllegalArgumentException("不存在全局变量[" + var + "]");
    }

    protected String textParse(String fieldHtml) {
        Jerry fieldJerry = Jerry.jerry(fieldHtml);
        String text = "";
        if (fieldJerry != null) {
            text = fieldJerry.text();
        }
        if (StringUtils.isNotBlank(text)) {
            text = text.trim();
        }
        return text;
    }

    protected String regParse(Map<String, Object> parse, String fieldHtml) {
        Pattern p = Pattern.compile((String) parse.get("reg"));
        Integer index = MapUtils.getInteger(parse, "index", 1);
        Matcher m = p.matcher(fieldHtml);
        if(m.find()){
            if(null == index || 0 == index){
                return m.group();
            }
            if(m.groupCount() >= index){
                return m.group(index);
            }
        }
        return "";
    }

    /**
     * <pre>
     *     根据开始-结束 标记对当前字段的html进行截取
     * </pre>
     * @param fieldHtml
     * @param locate         字段配置信息
     * @return  包含字段值的部分网页源码
     */
    protected String startEndLocate(String fieldHtml, Map<String, Object> locate) {
        Boolean containsStart = false;
        if (locate.containsKey("start-with")) {
            containsStart = (Boolean) locate.get("start-with");
        }
        Boolean containsEnd = false;
        if (locate.containsKey("end-with")) {
            containsEnd = (Boolean) locate.get("end-with");
        }
        String startText = "";
        if (locate.containsKey("start")) {
            startText = (String) locate.get("start");
        }
        if (StringUtils.isNotBlank(startText)) {
            int startIndex = fieldHtml.indexOf(startText);
            if (startIndex > 0) {
                if (!containsStart) {
                    startIndex = startIndex + startText.length();
                }
                fieldHtml = fieldHtml.substring(startIndex);
            } else {
                throw new IllegalArgumentException("不存在的开始 {" + startText + "}");
            }
        }
        String endText = "";
        if (locate.containsKey("end")) {
            endText = (String) locate.get("end");
        }
        if(StringUtils.isNotBlank(endText)) {
            int endIndex = fieldHtml.indexOf(endText);
            if(endIndex > 0){
                if(containsEnd){
                    endIndex = endIndex + endText.length();
                }
                fieldHtml = fieldHtml.substring(0, endIndex);
            } else {
                throw new IllegalArgumentException("不存在的结束 {" + endText + "}");
            }
        }
        return fieldHtml;
    }

    /**
     * css选择器
     * @param fieldHtml 当前店铺的页面源码
     * @param locate     当前字段的配置信息
     */
    protected String cssLocate(String fieldHtml, Map<String, Object> locate) {
        Jerry fieldJerry = jerry;
        if (StringUtils.isNotBlank(fieldHtml)) {
            fieldJerry = Jerry.jerry(fieldHtml);
        }
        fieldJerry = fieldJerry.$((String) locate.get("selector"));
        return fieldJerry.html();
    }

    public void setJerry(Jerry jerry) {
        this.jerry = jerry;
    }

    public void setHtml(String pageHtml) {
        this.pageHtml = pageHtml;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public void setBaseInfo(String[] baseInfo) {
        this.baseInfo = baseInfo;
    }

    public void setYamlFilename(String yamlFilename) {
        this.yamlFilename = yamlFilename;
    }

    public Map<String, String> getFieldNameMap() {
        return fieldNameMap;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }
}
