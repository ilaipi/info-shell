package com.richeninfo.xrzgather;

import com.richeninfo.xrzgather.kernel.page.Page;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

/**
 * 
 * @author mz
 * @date 2013-8-19 15:49:42
 */
public class HttpOperationTestCase {
    /**
     *
     */
//    @Test
//    public void testFaGaiWei(){
//        HttpOperation operation = new BaseComponents43Operation();
//        String html = operation.get("http://www.ndrc.gov.cn/zcfb/default.htm");
//        Document doc = Jsoup.parse(html);
//        Elements list = doc.select("d");
//        System.out.println(list.size());
//        Element e = list.first();
//        Element link = e.getElementsByTag("S").first();
//        System.out.println("link.href:" + link.ownText());
//    }
    
//    @Test
//    public void testJessery(){
//        HttpOperation operation = new BaseComponents43Operation();
//        String html = operation.get("http://www.ndrc.gov.cn/zcfb/default.htm");
//        Document doc = Jsoup.parse(html);
//        Elements list = doc.select("d");
//        System.out.println(list.size());
//        Element e = list.first();
//        Jerry j = Jerry.$("S", Jerry.jerry(e.outerHtml()));
//        System.out.println(j.size());
//    }
    
//    @Test
//    public void test31Huiyi1025(){
//        HttpOperation operation = new BaseComponents43Operation();
//        String html = operation.get("http://www.31huiyi.com/event/105185/");
//        System.out.println("----------------------------------------------------------------1");
//        System.out.println(html);
//        System.out.println("----------------------------------------------------------------2");
//        Jerry j = Jerry.$("#m__eventjoin", Jerry.jerry(html));
//        System.out.println(j.size());
//        
//        Document doc = Jsoup.parse(html);
//        Elements find = doc.select(".module_content-baoming");
//        System.out.println("find size:" + find.size());
//    }
    
//    @Test
//    public void testGXB() throws IOException{
////        HttpOperation operation = new BaseComponents43Operation();
////        String html = operation.get("http://www.miit.gov.cn/n11293472/n11293832/n12845605/n13916898/15590212.html");
////        CssLocate locate = new CssLocate();
////        locate.setCssSelector("td#Zoom2 p:nth-child(2)");
////        System.out.println(locate.locate(html));
//        
//        String regx = "\\$([^\\{]*)(\\{([^\\}]*)\\})?";
//        Pattern pattern = Pattern.compile("\\$([^\\{]*)(\\{([^\\}]*)\\})?");
//        Matcher matcher = pattern.matcher("$page");
//        if(matcher.find()){
//            System.out.println(matcher.group());
//            for(int i = 1;i<=matcher.groupCount();i++){
//                System.out.println("group " + i + ":" + matcher.group(i));
//            }
//        }
//    }
    
//    @Test
//    public void test36Kr(){
//        String url = "http://www.36kr.com/category/cn-news/2";
//        HttpOperation operation = new DefaultHttpOperation();
//        String html = operation.get(url);
//        System.out.println(html);
//    }
    
    /**
     * 中华人民共和国科学技术部
     * 意见征集
     */
//    @Test
//    public void testYJZJ(){
//        String url = "http://www.most.gov.cn/kjjh/yjzj/";
//        String html = new DefaultHttpOperation().get(url);
//        Document doc = Jsoup.parse(html);
//        Elements list = doc.select("div[ID=TRS]");
//        Element link = list.get(0);
//        //System.out.println(link.outerHtml());
//        Jerry j = Jerry.$("a", Jerry.jerry(link.outerHtml()));
//        html = j.get(0).getHtml();
//        System.out.println(html);
//        Pattern p = Pattern.compile("^<[^>]*>([^>]*)\\(\\d{4}\\-\\d{2}\\-\\d{2}\\)");
//        Matcher m = p.matcher(html);
//        if(m.find()){
//            System.out.println("group 0:" + m.group());
//            System.out.println("group 1:" + m.group(1));
//        }
//    }
//    @Test
//    public void testJYB(){
//        String url = "http://www.moe.gov.cn/publicfiles/business/htmlfiles/moe/moe_307/list.html";
//        String html = new DefaultHttpOperation().get(url);
//        html = html.replace("<script", "<div");
//        html = html.replace("</script>", "</div>");
//        Document doc = Jsoup.parse(html);
//        Elements find = doc.select("INFO");
//        System.out.println("find:" + find.size());
//    }
//    @Test
//    public void testJYB(){
//        String url = "http://gzly.miit.gov.cn:8080/opinion/noticedetail.do?method=notice_detail_show&noticeid=493";
//        String html = new BaseComponents43Operation().get(url);
//        System.out.println(html);
//    }
    @Test
    public void testPage() throws IllegalAccessException, InvocationTargetException{
        Page page = new Page();
        BeanUtils.setProperty(page, "firstPageNum", null);
        System.out.println(page.getFirstPageNum());
    }
}
