package com.richeninfo.xrzgather.view;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.View;

/**
 *
 * @author mz
 * @date 2013-8-12 14:58:27
 */
public class FlowDescView implements View{

    @Override
    public String getContentType() {
        return "text/html;charset=UTF-8";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String flowDesc = (String) model.get("flowDesc");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(flowDesc);
        response.getWriter().flush();
    }

}
