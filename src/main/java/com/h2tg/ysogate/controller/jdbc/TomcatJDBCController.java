package com.h2tg.ysogate.controller.jdbc;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;


@JNDIController
@JNDIMapping("/TomcatJDBC")
public class TomcatJDBCController extends SingleCommandController {
    public Object process(Properties props) {
        System.out.println("[Reference] Factory: TomcatJDBC");

        Reference ref = new Reference("javax.sql.DataSource", "org.apache.tomcat.jdbc.pool.DataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("url", props.getProperty("url")));
        ref.add(new StringRefAddr("initialSize", "1"));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("initSQL", props.getProperty("sql")));
        }

        return ref;
    }
}
