package A.R.ysogate.payloads.utils.jre;

import java.io.DataOutputStream;

public class TCNull implements SerializedElement {

	public void write(DataOutputStream out, HandleContainer handles) throws Exception {
		out.writeByte(112);
	}
}
