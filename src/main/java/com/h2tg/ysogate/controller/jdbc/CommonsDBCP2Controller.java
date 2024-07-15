package com.h2tg.ysogate.controller.jdbc;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.DatabaseController;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/CommonsDBCP2")
public class CommonsDBCP2Controller extends DatabaseController {
    public Object process(Properties props) {
        System.out.println("[Reference] Factory: CommonsDBCP2");

        Reference ref = new Reference("javax.sql.DataSource", "org.apache.commons.dbcp2.BasicDataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("url", props.getProperty("url")));
        ref.add(new StringRefAddr("initialSize", "1"));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("connectionInitSqls", props.getProperty("sql")));
        }

        return ref;
    }
}
