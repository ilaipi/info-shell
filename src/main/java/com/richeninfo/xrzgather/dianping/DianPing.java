package com.richeninfo.xrzgather.dianping;

import com.richeninfo.common.util.DateUtils;
import com.richeninfo.common.util.UrlUtils;
import com.richeninfo.http.DefaultHttpOperation;
import com.richeninfo.http.HttpOperation;
import jodd.jerry.Jerry;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ho.yaml.Yaml;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by yyam on 15-3-16.
 */
public class DianPing {
    public static final String PATH_PREFIX = "/home/yyam/workspace/info-shell/src/main/resources/dianping/";
    private Map<String, Integer> cityCodeMap = new HashMap<>();
    private Map<String, Map<String, String>> categoryList = new HashMap<>();
    protected HttpOperation operation = null;
    public static void main(String[] args) throws Exception {
        System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        DianPing dianPing = new DianPing();
        dianPing.initCityCode();
        dianPing.initCategoryList();
        dianPing.operation = new DefaultHttpOperation();
        String[][] shopInfo = dianPing.readShopList();
        String shopCity = "";//col A
        String shopName = "";//col B
        String shopLocate = "";//col C
        String urlBase = "http://www.dianping.com/search/keyword/";
        String url = "";
        String html = "";
        Jerry jerry = null;
        List<String> none = new ArrayList<>();
        List<String> none1 = new ArrayList<>();
        List<String> none2 = new ArrayList<>();
        List<String> one = new ArrayList<>();
        List<String> more = new ArrayList<>();
        List<String[]> shopList = new ArrayList<>();
        List<String> error = new ArrayList<>();
        for (int i = 0; i < shopInfo.length; i++) {
            shopCity = shopInfo[i][0];
            shopName = shopInfo[i][1];
            shopLocate = shopInfo[i][2];
            url = shopName;
            String info = (i + 3) + " " + shopCity + " " + shopName;
            if (StringUtils.isNotBlank(shopLocate) && (!"null".equals(shopLocate))) {
                url += " " + shopLocate;
                info += " " + shopLocate;
            }
            System.out.println(info);
            try {
                jerry = dianPing.search(urlBase, dianPing.cityCodeMap.get(shopCity), url);
                boolean not = false;
                if (jerry.size() == 0) {
                    none.add(info);
                    not = true;
                    jerry = dianPing.search(urlBase, dianPing.cityCodeMap.get(shopCity), shopName);
                }
                if (jerry.size() > 0 && not) {
                    none2.add(info);
                }
                switch (jerry.size()) {
                    case 0:
                        none1.add(info);
                        break;
                    case 1:
                        one.add(info);
                        break;
                    default:
                        more.add(info + " " + jerry.size());
                        break;
                }
                if (jerry.size() > 0) {
                    shopInfo[i][3] = jerry.$("div.txt>div.tit a").attr("href");
                    shopInfo[i][3] = UrlUtils.getAbsoluteUrl(urlBase + dianPing.cityCodeMap.get(shopCity) + "/0_", shopInfo[i][3]);
                    shopList.add(shopInfo[i]);
                }
                Thread.sleep(5 * 1000L);
            } catch (Exception e) {
                error.add(info);
                e.printStackTrace();
                continue;
            }
//            if (i == 19) {
//                break;
//            }
        }
        System.out.println("空列表总数：" + none.size());
        System.out.println("空列表1(shopName)：" + none1.size());
        for (String shop : none1) {
            System.out.println(shop);
        }
        System.out.println("空列表2(shopName + shopLocate)：" + none2.size());
        for (String shop : none2) {
            System.out.println(shop);
        }
        System.out.println("单个结果个数：" + one.size());
        System.out.println("多个结果个数：" + more.size());
        for (String shop : more) {
            System.out.println(shop);
        }

        System.out.println("///////////////////////////////");
        Jerry page = null;
        Set<String> categories = new HashSet<>();
        Map<String, String> fieldNameMap = null;
        List<String> fieldNames = null;
        File output = new File(PATH_PREFIX + "特惠商户.xls");
        if (!output.exists()) {
            output.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(output);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = null;
        HSSFRow row = null;
        Map<String, String> shopResult = null;
        for (String[] shop : shopList) {
            html = dianPing.operation.get(shop[3], null, null);
            page = Jerry.jerry(html);
            jerry = page.$("div.breadcrumb a");
            if (jerry.size() > 0) {
                int length = jerry.size();
                if (jerry.size() >= 6) {
                    length = 6;
                }
                for (int i = 0; i < length; i++) {
                    shop[i + 4] = jerry.get(i).getTextContent().trim();
                }
            }
            System.out.println(ArrayUtils.toString(shop));
            String category = StringUtils.substring(shop[4], 2);
            categories.add(category);
            CategoryGather categoryGather = new CategoryGather();
            categoryGather.pageHtml = html;
            categoryGather.baseInfo = shop;
            try {
                Map<String, String> mainCategory = dianPing.findCategory(shop[4]);
                categoryGather.mainCategory = mainCategory.get("MainCategory");
                categoryGather.yamlFilename = mainCategory.get("yaml");
                shopResult = categoryGather.gather();
                fieldNameMap = categoryGather.getFieldNameMap();
                fieldNames = categoryGather.getFieldNames();
                sheet = dianPing.getMainCategorySheet(workbook, categoryGather.mainCategory, fieldNames, fieldNameMap);
                dianPing.writeShopInfo(sheet, fieldNames, shopResult, shop);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(5 * 1000L);
        }

        workbook.write(outputStream);
        workbook.close();

        System.out.println("=============================");
        System.out.println("店铺大分类：");
        System.out.println(ArrayUtils.toString(categories.toArray()));
        System.out.println(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    private void writeShopInfo(HSSFSheet sheet, List<String> fieldNames, Map<String, String> shop, String[] baseInfo) {
        int rowNum = sheet.getLastRowNum() + 1;
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell cell = null;
        for (int i = 0; i < 4; i++) {
            cell = row.createCell(i);
            cell.setCellValue(baseInfo[i]);
        }
        for (int i = 0; i < fieldNames.size(); i++) {
            cell = row.createCell(i + 4);
            cell.setCellValue(shop.get(fieldNames.get(i)));
        }
    }

    private HSSFSheet getMainCategorySheet(HSSFWorkbook workbook, String sheetName, List<String> fieldNames, Map<String, String> fieldNameMap) {
        HSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet != null) {
            return sheet;
        }
        sheet = workbook.createSheet(sheetName);
        HSSFRow head = sheet.createRow(0);
        HSSFCell cell = null;
        for (int i = 0; i < fieldNames.size(); i++) {
            cell = head.createCell(i + 4);
            cell.setCellValue(fieldNameMap.get(fieldNames.get(i)));
        }
        return sheet;
    }

    private Jerry search(String urlBase, Integer cityCode, String url) throws UnsupportedEncodingException {
        url = urlBase + cityCode + "/0_" + URLEncoder.encode(url, "UTF-8");
        String html = operation.get(url, null, null);
        Jerry jerry = Jerry.jerry(html);
        jerry = jerry.$("#shop-all-list li");
        return jerry;
    }

    private void initCityCode() throws FileNotFoundException {
        cityCodeMap.putAll((Map<? extends String, ? extends Integer>) Yaml.load(new File(PATH_PREFIX + "city_list.yaml")));
    }

    private void initCategoryList() throws FileNotFoundException {
        categoryList.putAll((Map<? extends String, Map<String, String>>) Yaml.load(new File(PATH_PREFIX + "category_list.yaml")));
    }

    private String[][] readShopList() throws Exception {
        String shopListFile = PATH_PREFIX + "shop_list.yaml";
        List<String> shopList = (List<String>) Yaml.load(new File(shopListFile));
        String[][] shopInfo = new String[shopList.size()][];
        String[] shop = null;
        for (int i = 0; i < shopList.size(); i++) {
            shop = StringUtils.split(shopList.get(i), " ");
            shopInfo[i] = new String[10];
            for (int j = 0; j < shop.length; j++) {
                shopInfo[i][j] = shop[j];
            }
        }
        return shopInfo;
    }

    private Map<String, String> findCategory(String mainCategory) {
        Map<String, String> containsCategory = null;
        Map<String, String> equalsCategory = null;
        for (Map.Entry<String, Map<String, String>> entry : categoryList.entrySet()) {
            if (mainCategory.contains(entry.getKey())) {
                containsCategory = entry.getValue();
            }
            if (mainCategory.equals(entry.getKey())) {
                equalsCategory = entry.getValue();
                break;
            }
        }
        if (equalsCategory != null) {
            return equalsCategory;
        }
        return containsCategory;
    }
}
