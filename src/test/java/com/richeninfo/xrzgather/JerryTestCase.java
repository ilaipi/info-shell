package com.richeninfo.xrzgather;

import com.richeninfo.xrzgather.kernel.Info;
import com.richeninfo.xrzgather.kernel.field.fieldvalue.PlainText;
import com.richeninfo.xrzgather.kernel.field.handler.Huiyi31TitleHandler;
import com.richeninfo.xrzgather.kernel.resulthandler.AttachmentFromInfoTextHandler;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * 
 * @author mz
 * @date 2013-8-20 10:22:14
 */
public class JerryTestCase {
    @Test
    public void testJerry() throws IOException{
//        AttachmentFromInfoTextHandler handler = Mockito.mock(AttachmentFromInfoTextHandler.class);
        AttachmentFromInfoTextHandler handler = new AttachmentFromInfoTextHandler();
        //handler.setAttachSelector("a");
        Map<String, Object> currResult = new HashMap<>();
        currResult.put(Info.INFO_TEXT.name(), FileUtils.readFileToString(new File("D:/1.txt"), "UTF-8"));
        currResult.put(Info.INFO_URL.name(), "http://www.ndrc.gov.cn/zcfb/zcfbqt/2013qt/t20130806_552923.htm");
        handler.handle(currResult);
    }
    
    @Test
    public void test31HuiyiTitle(){
        String locateHtml = "<a href=\"/event/96969/\"  target=\"_blank\" title=\"格桑泽仁：得觉催眠，唤醒幸福人生&lt;深圳站&gt;\">格桑泽仁：得觉催眠，唤醒幸福人生&lt;深圳站&gt;</a></h3>";
        PlainText pt = new PlainText();
        pt.setLocateHtml(locateHtml);
        String value = pt.getValue();
        System.out.println(value);
        Huiyi31TitleHandler handler = new Huiyi31TitleHandler();
        System.out.println(handler.handle(value));
    }
}
