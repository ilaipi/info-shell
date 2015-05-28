package com.richeninfo.xrzgather.kernel.locate;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mz
 * @date 2013-8-8 12:26:55
 */
public class BeginAndEndTextLocate extends Locate{
    private String startText;
    private Boolean containsStart;
    private String endText;
    private Boolean containsEnd;
    
    @Override
    public String locate(String html){
        if(StringUtils.isNotBlank(startText)){
            int startIndex = html.indexOf(startText);
            if(startIndex > 0){
                if(!getContainsStart()){
                    startIndex = startIndex + startText.length();
                }
                html = html.substring(startIndex);
            } else {
                throw new IllegalArgumentException("不存在的开始 {" + startText + "}");
            }
        }
        if(StringUtils.isNotBlank(endText)){
            int endIndex = html.indexOf(endText);
            if(endIndex > 0){
                if(getContainsEnd()){
                    endIndex = endIndex + endText.length();
                }
                html = html.substring(0, endIndex);
            } else {
                throw new IllegalArgumentException("不存在的结束 {" + endText + "}");
            }
        }
        return html;
    }

    public String getStartText() {
        return startText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public Boolean getContainsStart() {
        if(containsStart == null){
            containsStart = Boolean.FALSE;
        }
        return containsStart;
    }

    public void setContainsStart(Boolean containsStart) {
        this.containsStart = containsStart;
    }

    public String getEndText() {
        return endText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public Boolean getContainsEnd() {
        if(containsEnd == null){
            containsEnd = Boolean.FALSE;
        }
        return containsEnd;
    }

    public void setContainsEnd(Boolean containsEnd) {
        this.containsEnd = containsEnd;
    }
}
