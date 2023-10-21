package A.R.ysogate.payloads.utils;

import javassist.CannotCompileException;
import javassist.CtClass;

import static A.R.ysogate.payloads.config.Config.POOL;

public class Utils
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
        CtClass ctClass = POOL.makeClass(clazzName);
        Class   clazz   = null;
        try {
            clazz = ctClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }
        ctClass.defrost();
        return clazz;
    }
}
