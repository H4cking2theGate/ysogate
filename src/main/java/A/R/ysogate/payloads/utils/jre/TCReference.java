package A.R.ysogate.payloads.utils.jre;

import java.io.DataOutputStream;

public class TCReference implements SerializedElement {

	private int handle;

	public TCReference(int handle) {
		this.handle = handle;
	}

	public void write(DataOutputStream out, HandleContainer handles) throws Exception {
		out.writeByte(113);
		out.writeInt(8257536 + this.handle);
	}
}
