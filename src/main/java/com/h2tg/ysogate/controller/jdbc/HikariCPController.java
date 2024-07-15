package com.h2tg.ysogate.controller.jdbc;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/HikariCP")
public class HikariCPController extends SingleCommandController {
    public Object process(Properties props) {
        System.out.println("[Reference] Factory: HikariCP");

        Reference ref = new Reference("javax.sql.DataSource", "com.zaxxer.hikari.HikariJNDIFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("jdbcUrl", props.getProperty("url")));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("connectionInitSql", props.getProperty("sql")));
        }

        return ref;
    }
}
