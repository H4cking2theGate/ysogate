package com.h2tg.ysogate.utils;

/**
 * @author su18
 */
public class SuClassLoader extends ClassLoader {

    public SuClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }
}
