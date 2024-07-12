package com.h2tg.ysogate.utils.jre;

import java.io.DataOutputStream;

public interface SerializedElement {

	void write(DataOutputStream paramDataOutputStream, HandleContainer paramHandleContainer) throws Exception;
}
