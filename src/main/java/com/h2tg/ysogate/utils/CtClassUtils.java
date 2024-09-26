package com.h2tg.ysogate.utils;

import com.h2tg.ysogate.config.Config;
import javassist.*;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.SourceFileAttribute;

import java.util.List;

/**
 * @author su18
 */
public class CtClassUtils
{

    public static boolean hasSerialVersionUID(CtClass ctClass) {
        try {
            ctClass.getDeclaredField("serialVersionUID");
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
    public static void setCtField(CtClass clazz, String name, CtField.Initializer value) throws Exception {
        CtField ctField = clazz.getField(name);
        clazz.removeField(ctField);
        clazz.addField(ctField, value);
    }

    public static void insertField(CtClass ctClass, String fieldName, String fieldCode) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
        } catch (javassist.NotFoundException ignored) {
        }
        ctClass.addField(CtField.make(fieldCode, ctClass));
    }

    public static void addMethod(CtClass ctClass, String methodName, String methodBody) throws Exception {
        ctClass.defrost();
        try {
            // 已存在，修改
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
            ctMethod.setBody(methodBody);
        } catch (NotFoundException ignored) {
            // 不存在，直接添加
            CtMethod method = CtNewMethod.make(methodBody, ctClass);
            ctClass.addMethod(method);
        }
    }

    public static String bypassJDKModuleBody() throws Exception {
        return "{try {\n" +
                "            Class unsafeClass = Class.forName(\"sun.misc.Unsafe\");\n" +
                "            java.lang.reflect.Field unsafeField = unsafeClass.getDeclaredField(\"theUnsafe\");\n" +
                "            unsafeField.setAccessible(true);\n" +
                "            Object unsafe = unsafeField.get(null);\n" +
                "            java.lang.reflect.Method getModuleM = Class.class.getMethod(\"getModule\", new Class[0]);\n" +
                "            Object module = getModuleM.invoke(Object.class, (Object[]) null);\n" +
                "            java.lang.reflect.Method objectFieldOffsetM = unsafe.getClass().getMethod(\"objectFieldOffset\", new Class[]{java.lang.reflect.Field.class});\n" +
                "            java.lang.reflect.Field moduleF = Class.class.getDeclaredField(\"module\");\n" +
                "            Object offset = objectFieldOffsetM.invoke(unsafe, new Object[]{moduleF});\n" +
                "            java.lang.reflect.Method getAndSetObjectM = unsafe.getClass().getMethod(\"getAndSetObject\", new Class[]{Object.class, long.class, Object.class});\n" +
                "            getAndSetObjectM.invoke(unsafe, new Object[]{this.getClass(), offset, module});\n" +
                "        } catch (Exception ignored) {\n" +
                "        }}";
    }

    // 删除 SourceFileAttribute (源文件名) 信息
    public static void removeSourceFileAttribute(CtClass ctClass) {
        ctClass.defrost();
        ClassFile classFile = ctClass.getClassFile2();

        try {
            // javassist.bytecode.ClassFile.removeAttribute  Since: 3.21
            Reflections.getMethodAndInvoke(classFile, "removeAttribute", new Class[]{String.class}, new Object[]{SourceFileAttribute.tag});
        } catch (Exception e) {
            try {
                // 兼容 javassist v3.20 及以下
                List<AttributeInfo> attributes = (List<AttributeInfo>) Reflections.getFieldValue(classFile, "attributes");
                removeAttribute(attributes, SourceFileAttribute.tag);
            } catch (Exception ignored) {
            }
        }
    }

    public static synchronized AttributeInfo removeAttribute(List<AttributeInfo> attributes, String name) {
        if (attributes == null) return null;

        for (AttributeInfo ai : attributes)
            if (ai.getName().equals(name)) if (attributes.remove(ai)) return ai;

        return null;
    }
    /**
     * 将 Field String 转一层，实际用处不大
     *
     * @param target Field String 值
     * @return 替换后的 String
     */
    public static String converString(String target) {
        if (Config.IS_OBSCURE) {
            StringBuilder result = new StringBuilder("new String(new byte[]{");
            byte[]        bytes  = target.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                result.append(bytes[i]).append(",");
            }
            return result.substring(0, result.length() - 1) + "})";
        }

        return "\"" + target + "\"";
    }

    /**
     * 只有在原 Class 已经有此 Field 的情况下才加入
     *
     * @param ctClass   CtClass
     * @param fieldName field 名称
     * @param fieldCode field 代码
     * @throws Exception 抛出异常
     */
    public static void insertFieldIfExists(CtClass ctClass, String fieldName, String fieldCode) throws Exception {
        ctClass.defrost();
        try {
            CtField field = ctClass.getDeclaredField(fieldName);
            ctClass.removeField(field);
            ctClass.addField(CtField.make(fieldCode, ctClass));
        } catch (javassist.NotFoundException ignored) {
        }
    }

}
