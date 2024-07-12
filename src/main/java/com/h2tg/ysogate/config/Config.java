package com.h2tg.ysogate.config;

import javassist.ClassPool;

public class Config {


    // 是否使用混淆技术
    public static Boolean IS_OBSCURE = false;

    // 恶意类是否继承 AbstractTranslet
    public static Boolean IS_INHERIT_ABSTRACT_TRANSLET = false;


    // 是否强制使用 org.apache.XXX.TemplatesImpl
    public static Boolean USING_ORG_APACHE_XALAN = false;

    // 将输入直接写在文件里
    public static String FILE = "out.ser";

    public static Boolean WRITE_FILE = false;

    public static Boolean BASE64_ENCODE = false;

    public static Boolean IS_BYTECODES = false;

    public static ClassPool POOL = ClassPool.getDefault();

}
