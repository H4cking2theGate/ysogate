package com.h2tg.ysogate;

import org.junit.Test;
import static org.apache.tomcat.util.codec.binary.Base64.isBase64;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Base64;

public class CiTest {
    @Test
    public void test() {
        System.out.println("System.getProperties(): " + System.getProperties());
        System.out.println("System.getenv(): " + System.getenv());
    }

//    @Test
//    public void jndi() throws NamingException
//    {
//        String gadget = "CommonsBeanutils2183NOCC";
//        Context ctx = new InitialContext();
//        Object result = ctx.lookup("ldap://127.0.0.1:1389/Deserialize/"+gadget+"/Command/calc");
//    }
}