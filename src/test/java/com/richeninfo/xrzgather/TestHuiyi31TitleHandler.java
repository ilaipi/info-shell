package com.richeninfo.xrzgather;

import com.richeninfo.xrzgather.kernel.field.handler.Huiyi31TitleHandler;
import org.junit.Test;

/**
 * 
 * @author mz
 * @date 2013-8-21 11:00:58
 */
public class TestHuiyi31TitleHandler {
    @Test
    public void testHandle(){
        Huiyi31TitleHandler handler = new Huiyi31TitleHandler();
        String value = "格桑泽仁：得觉催眠，唤醒幸福人生<深圳站>";
        System.out.println(handler.handle(value));
    }
}
