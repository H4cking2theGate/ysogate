package com.h2tg.ysogate.utils;

import com.h2tg.ysogate.config.Config;
import javassist.CannotCompileException;
import javassist.CtClass;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class MiscUtils
{
    public static String base64Encode(byte[] bs) throws Exception {
        Class  base64;
        String value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", new Class[]{}).invoke(null, (Object[]) null);
            value = (String) Encoder.getClass().getMethod("encodeToString", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String) Encoder.getClass().getMethod("encode", new Class[]{byte[].class}).invoke(Encoder, new Object[]{bs});
            } catch (Exception e2) {
            }
        }
        return value;
    }


    public static String base64Decode(String bs) throws Exception {
        Class  base64;
        byte[] value = null;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", new Class[]{}).invoke(null, (Object[]) null);
            value = (byte[]) decoder.getClass().getMethod("decode", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
        } catch (Exception e) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[]) decoder.getClass().getMethod("decodeBuffer", new Class[]{String.class}).invoke(decoder, new Object[]{bs});
            } catch (Exception ignored) {
            }
        }

        return new String(value);
    }

    public static Class makeClass(String clazzName) {
        CtClass ctClass = Config.POOL.makeClass(clazzName);
        Class   clazz   = null;
        try {
            clazz = ctClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
        ctClass.defrost();
        return clazz;
    }

    public static String getRandStr(int length){
        String dicts = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i ++) {
            int index = random.nextInt(dicts.length());
            sb.append(dicts.charAt(index));
        }

        return "Exploit_" + sb;
    }



    public static String tryBase64UrlDecode(String encText) {
        try {
            // 判断字符串是否使用 Base64 URL 编码
            // 方法: 先 decode 再重新 encodeToString, 判断两者是否相等
            byte[] decBytes = Base64.getUrlDecoder().decode(encText);

            for (byte b : decBytes) {
                if (b < 32 || b > 126) {
                    return encText;
                }
            }

            String reEncText = Base64.getUrlEncoder().encodeToString(decBytes);

            if (reEncText.equals(encText)) {
                // Base64 URL 编码

                // 判断 Base64 URL 解码结果是否属于纯文本内容
                // 方法: 先将 byte[] 转成 String (标准化), 然后 getBytes 重新获取 byte[], 判断两者是否相等
                String decText = new String(decBytes);
                byte[] reDecBytes = decText.getBytes();

                if (Arrays.equals(reDecBytes, decBytes)) {
                    // 纯文本内容
                    return decText;
                } else {
                    // 非纯文本内容
                    return encText;
                }
            } else {
                // 非 Base64 URL 编码
                return encText;
            }
        } catch (Exception e) {
            // 非 Base64 URL 编码
            return encText;
        }
    }
}
