package com.h2tg.ysogate.bullet.defineClass;

public class OgnlConverter
{
    public static String ognl2js(String js)
    {
        String code = "(new javax.script.ScriptEngineManager()).getEngineByName(\"js\").eval(\""+js+"\")";
        return code;
    }
}
