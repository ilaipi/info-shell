package com.richeninfo.xrzgather.utils;

import com.richeninfo.http.HttpOperation;
import com.richeninfo.http.utils.HttpOperationUtils;
import jodd.jerry.Jerry;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author mz
 * @date 2013-8-14 11:41:26
 */
public class MultiPageBean {
    private String beforePage;
    private String afterPage;
    
    private String cssSelector;
    
    private HttpOperation operation;
    
    private MultiPageType multiPageType;
    
    enum MultiPageType{
        BEFORE_AND_AFTER_PAGE,
        CSS_SELECTOR,
        FIEXD
    }
    
    public MultiPageBean setOperation(HttpOperation operation){
        this.operation = operation;
        return this;
    }
    
    private MultiPageBean setMultiPageType(MultiPageType multiPageType){
        this.multiPageType = multiPageType;
        return this;
    }
    
    public static MultiPageBean build(String cssSelector){
        return  new MultiPageBean(cssSelector).setMultiPageType(MultiPageType.CSS_SELECTOR);
    }
    
    public static MultiPageBean build(String beforePage, String afterPage){
        return new MultiPageBean(beforePage, afterPage).setMultiPageType(MultiPageType.BEFORE_AND_AFTER_PAGE);
    }
    public static MultiPageBean build(){
        return new MultiPageBean().setMultiPageType(MultiPageType.FIEXD);
    }
    
    /**
     * 如果 cssSelector 为空，则使用 页码前、页码后的分页方式。
     * 否则，根据 cssSelector 找到a标签，解析href
     * @param beforePage
     * @param afterPage
     * @param cssSelector
     * @return 
     */
    public static MultiPageBean build(String beforePage, String afterPage, String cssSelector){
        if(StringUtils.isBlank(beforePage) && StringUtils.isBlank(cssSelector)){
            return build();
        }
        if(StringUtils.isBlank(cssSelector)){
            return build(beforePage, afterPage);
        } else{
            return build(cssSelector);
        }
    }
    
    /**
     * 此方法首先调用 nextPageUrl 方法获取下页的链接，然后发送get请求
     * @see #nextPageUrl(java.lang.String, java.lang.Integer, java.lang.String) 
     * @param currPageUrl
     * @param currPageNum
     * @param currPageHtml
     * @return 
     */
    public String nextPageHtml(String currPageUrl, Integer currPageNum, String currPageHtml) {
        String nextPageUrl = nextPageUrl(currPageUrl, currPageNum, currPageHtml);
        return operation.get(nextPageUrl, null, null);
    }
    
    public String nextPageUrl(String currPageUrl, Integer currPageNum, String currPageHtml) {
        String resultUrl = "";
        switch(multiPageType){
            case CSS_SELECTOR:
                Jerry j = Jerry.$(cssSelector, Jerry.jerry(currPageHtml));
                if(j.size() > 0){
                    j = Jerry.$("a", Jerry.jerry(j.get(0).getHtml()));
                    if(j.size() > 0){
                        String href = j.attr("href");
                        resultUrl = HttpOperationUtils.getAbsoluteUrl(currPageUrl, href);
                    } else{
                        throw new IllegalArgumentException("不能根据分页选择器找到分页标签！分页选择器：" + cssSelector);
                    }
                } else{
                    throw new IllegalArgumentException("不能根据分页选择器找到分页标签！分页选择器：" + cssSelector);
                }
                break;
            case BEFORE_AND_AFTER_PAGE:
                resultUrl = beforePage + (currPageNum + 1) + afterPage;
                break;
            case FIEXD:
                resultUrl = currPageUrl;
                break;
        }
        return resultUrl;
    }
    
    public static void main(String[] args) {
        String html = "<a href='javascript:void(0);'>haha</a>";
        Element e = Jsoup.parse(html);
        System.out.println(e.getElementsByTag("a").size());
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

    public String getCssSelector() {
        return cssSelector;
    }

    public void setCssSelector(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    private MultiPageBean(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    private MultiPageBean() {
    }

    private MultiPageBean(String beforePage, String afterPage) {
        this.beforePage = beforePage;
        this.afterPage = afterPage;
    }

    private MultiPageBean(String beforePage, String afterPage, String cssSelector) {
        this.beforePage = beforePage;
        this.afterPage = afterPage;
        this.cssSelector = cssSelector;
    }
}
