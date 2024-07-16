package com.h2tg.ysogate.controller.jdbc;

import com.h2tg.ysogate.annotation.JNDIController;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.DatabaseController;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import java.util.Properties;

@JNDIController
@JNDIMapping("/Druid")
public class DruidController extends DatabaseController {
    @Override
    public Object process(Object obj) {
        Properties props = (Properties)obj;
        System.out.println("[Reference] Factory: Druid");

        Reference ref = new Reference("javax.sql.DataSource", "com.alibaba.druid.pool.DruidDataSourceFactory", null);
        ref.add(new StringRefAddr("driverClassName", props.getProperty("driver")));
        ref.add(new StringRefAddr("url", props.getProperty("url")));
        ref.add(new StringRefAddr("initialSize", "1"));
        ref.add(new StringRefAddr("init", "true"));

        if (props.getProperty("sql") != null) {
            ref.add(new StringRefAddr("initConnectionSqls", props.getProperty("sql")));
        }

        return ref;
    }
}
