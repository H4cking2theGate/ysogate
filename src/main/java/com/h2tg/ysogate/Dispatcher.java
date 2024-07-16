package com.h2tg.ysogate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.Controller;
import com.h2tg.ysogate.utils.MiscUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.LoggerFactory;
import static com.h2tg.ysogate.utils.Reflections.getMethodByClass;
import static com.h2tg.ysogate.utils.Reflections.getMethodAndInvoke;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dispatcher
{
    private static Dispatcher INSTANCE = new Dispatcher();
    private Map<Class<?>, Controller> controllersMap = new HashMap<>();

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    private Dispatcher() {
        // 关闭 Reflections 包的日志输出
        Logger root = (Logger) LoggerFactory.getLogger("org.reflections");
        root.setLevel(Level.OFF);

        // 扫描所有使用 JNDIController 注解的类
        Reflections ref = new Reflections("com.h2tg.ysogate.controller",
                new TypeAnnotationsScanner(), new MethodAnnotationsScanner());
        Set<Class<?>> controllerClasses = ref.getTypesAnnotatedWith(JNDIController.class);

        // 初始化 controllersMap
        for (Class<?> clazz : controllerClasses) {
            try {
                controllersMap.put(clazz, (Controller) clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Object service(String path) {
        for (Map.Entry<Class<?>, Controller> entry : controllersMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            Controller controller = entry.getValue();

            // 获取类的 JNDIMapping 注解
            JNDIMapping baseMapping = clazz.getAnnotation(JNDIMapping.class);
            String basePath = (baseMapping != null) ? baseMapping.value() : "";
            Method[] methods = clazz.getMethods();
            Method processMethod = getMethodByClass(clazz, "process", new Class[]{Object.class});

//            for (Method method : methods) {
//                if ("process".equals(method.getName())) {
//                    processMethod = method;
//                    break;
//                }
//            }

            for (Method method : methods) {
                // 获取方法的 JNDIMapping 注解
                JNDIMapping methodMapping = method.getAnnotation(JNDIMapping.class);

                // 匹配路由
                if (methodMapping != null) {
                    String mappingPath = basePath + methodMapping.value();
                    String regex = mappingPath.replaceAll("\\{.*?\\}", "([^/]+)");
                    Pattern valuePattern = Pattern.compile("^" + regex + "$");
                    Matcher valueMatcher = valuePattern.matcher(path); // 提取参数值

                    if (valueMatcher.matches()) {
                        Pattern namePattern = Pattern.compile("\\{(.*?)\\}");
                        Matcher nameMatcher = namePattern.matcher(mappingPath); // 提取参数名
                        List params = new ArrayList(); // 存放匹配的参数值

                        int groupIndex = 1;
                        while (nameMatcher.find()) {
                            String value = valueMatcher.group(groupIndex);
                            value = MiscUtils.tryBase64UrlDecode(value); // 自动对纯文本内容进行 Base64 URL 解码
                            groupIndex ++;
                            params.add(value); // 将参数存入 params 列表
                        }

                        try {
                            Object obj = method.invoke(controller, params.toArray()); // 调用与路由相对应的方法
                            return getMethodAndInvoke(controller, "process", new Class[]{Object.class},new Object[]{obj}); // 调用 Controller 的 process 方法
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
}
