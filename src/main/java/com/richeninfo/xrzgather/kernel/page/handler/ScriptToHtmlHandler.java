package com.richeninfo.xrzgather.kernel.page.handler;

/**
 * 
 * @author mz
 * @date 2013-8-22 20:42:06
 */
public class ScriptToHtmlHandler extends PageHandler{

    @Override
    public String handle(String srcHtml) {
        return srcHtml.replace("<script", "<div").replace("</script>", "</div>");
    }
    
}
