## ysogate

ysogate是一个java综合利用工具，支持JNDI注入相关利用，包含多种高版本jdk绕过方式，且支持片段化gadget生成和组合。

- 生成多种Java反序列化gadget payload
- 支持JNDI/LDAP/RMI/JRMP等多种利用方式
- 灵活的命令行界面，支持多种操作模式
- 可扩展的架构，便于添加新的gadget和攻击向量
- 支持多种高版本jdk绕过方式

### Usage

```bash
[root]#~  Payload Mode: java -jar ysogate-[version]-all.jar -m payload [PAYLOAD OPTIONS]
[root]#~  JNDI Mode:    java -jar ysogate-[version]-all.jar -m jndi [JNDI OPTIONS]
```
### JNDI

### 反序列化

### 扩展攻击

#### 回显

#### 内存马

### todo
- [x] 基础的反序列化生成payload
- [x] 增加JNDI/LDAP/RMI/JRMP等利用方式
- [ ] 完善第三方库的gadget，hutool，fastjson，jackson，commons-collections等
- [ ] 在加载字节码方面增加扩展攻击如回显，内存马，代理等
- [ ] 防护绕过方面的补充，增加OverlongUTF8/脏数据等绕过
- [ ] 代码结构优化

### 免责声明
本项目仅面向安全研究与学习，禁止任何非法用途

如您在使用本项目的过程中存在任何非法行为，您需自行承担相应后果

除非您已充分阅读、完全理解并接受本协议，否则，请您不要使用本项目

### 参考
 - https://github.com/frohoff/ysoserial
 - https://github.com/X1r0z/JNDIMap
 - ysuserial