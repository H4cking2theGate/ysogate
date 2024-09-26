package com.h2tg.ysogate.config;

import java.util.Objects;

import static com.h2tg.ysogate.utils.RandomUtils.getRandomClassName;

public class GenConfig {
    public static String className=getRandomClassName();
    public static String formatType="base64";
    public static String reqHeaderName="cmd";
    public static boolean bypassModule=false;
    public static String reqParamName;
    public static String respHeaderName;
    public static String base64ClassString;
    public static byte[] classBytes;
    public static int classBytesLength;
    public static boolean implementsASTTransformationType = false;
    public static boolean implementsScriptEngineFactory = false;
    public static String loaderClassName;
    public static String gadgetType;


}
