package com.h2tg.ysogate.template;

import java.io.IOException;

public class CalcTemplate
{
    static {
        try{
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
