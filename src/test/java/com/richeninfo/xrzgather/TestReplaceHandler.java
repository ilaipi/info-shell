package com.richeninfo.xrzgather;

import com.richeninfo.xrzgather.kernel.field.handler.ReplaceHandler;
import org.junit.Test;

/**
 * 
 * @author mz
 * @date 2013-8-25 14:47:39
 */
public class TestReplaceHandler {
    @Test
    public void testReplace(){
        String replaces = "zcfbgg/ zcfbqt/ zcfbtz/";
        String replaceWiths = "http://www.ndrc.gov.cn/zcfb/zcfbgg/ http://www.ndrc.gov.cn/zcfb/zcfbqt/ http://www.ndrc.gov.cn/zcfb/zcfbtz/";
        String value = "zcfbqt/2013qt/t20130806_552923.htm";
        ReplaceHandler handler = new ReplaceHandler();
        handler.setIsReg(false);
        handler.setReplaceWith(replaceWiths);
        handler.setToReplace(replaces);
        value = handler.handle(value);
        assert value.equals("http://www.ndrc.gov.cn/zcfb/zcfbqt/2013qt/t20130806_552923.htm");
    }
}
