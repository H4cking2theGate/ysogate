# ysogate

<p align="center">
  <img src="https://img.shields.io/badge/Java-%3E%3D8-blue" alt="Java">
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License">
</p>

ysogate是一个功能强大的Java安全测试工具，主要用于生成各种Java反序列化gadget payload和JNDI注入利用。该工具支持多种绕过技术，适用于高版本JDK环境，并提供片段化gadget生成和组合功能。

## 功能特性

- ✅ 生成多种Java反序列化gadget payload
- ✅ 支持JNDI/LDAP/RMI/JRMP等多种利用方式
- ✅ 灵活的命令行界面，支持多种操作模式
- ✅ 可扩展的架构，便于添加新的gadget和攻击向量
- ✅ 支持多种高版本JDK绕过方式
- ✅ 支持扩展利用方式，如内存马、回显、代理等
- ✅ 提供恶意类生成功能，支持多种中间件/框架

## 目录结构

```
ysogate/
├── README.md
├── docs/
│   ├── PayloadMode.md
│   ├── JNDIMode.md
│   └── GenMode.md
├── src/
│   └── main/
│       └── java/
│           └── com/h2tg/ysogate/
└── pom.xml
```

## 安装与构建

```bash
# 克隆项目
git clone https://github.com/H4cking2theGate/ysogate.git

# 进入项目目录
cd ysogate

# 使用Maven构建项目
mvn clean package

# 构建完成后，可在target目录找到可执行jar包
ls target/ysogate-*-all.jar
```

## 使用方法

ysogate支持三种操作模式：

1. **Payload Mode** (`-m payload`) - 生成反序列化payload
2. **JNDI Mode** (`-m jndi`) - 启动JNDI服务器
3. **Gen Mode** (`-m gen`) - 生成恶意类

### 快速开始

```bash
# 查看帮助信息
java -jar ysogate-[version]-all.jar -h

# 查看各模式的详细帮助
java -jar ysogate-[version]-all.jar -m payload -h
java -jar ysogate-[version]-all.jar -m jndi -h
java -jar ysogate-[version]-all.jar -m gen -h
```

## 模式详解

### Payload Mode

生成自定义的反序列化payload，例如输出base64编码的payload：

```bash
java -jar ysogate-[version]-all.jar -m payload -g Jackson1 -p calc -b64
```

更多用法请参考 [PayloadMode](docs/PayloadMode.md)

### JNDI Mode

在本地运行恶意的JNDI服务器：

```bash
java -jar ysogate-[version]-all.jar -m jndi -i 0.0.0.0 -onlyRef
```

更多用法请参考 [JNDIMode](docs/JNDIMode.md)

### Gen Mode

生成恶意类，例如生成Spring MVC的命令执行回显：

```bash
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CmdExec -name org.springframework.expression.Evil -bypass
```

更多用法请参考 [GenMode](docs/GenMode.md)

## 支持的Gadgets

ysogate支持多种反序列化gadgets，包括但不限于：

- CommonsBeanutils系列
- CommonsCollections系列
- C3P0系列
- Fastjson系列
- Jackson系列
- Spring系列
- Hibernate系列
- 以及更多...

运行以下命令查看完整的gadget列表：

```bash
java -jar ysogate-[version]-all.jar -m payload -h
```

## 开发指南

### 添加新的Gadget

1. 在`src/main/java/com/h2tg/ysogate/payloads/gadgets/`目录下创建新的gadget类
2. 实现`CommandObjectPayload`接口
3. 添加必要的依赖注解
4. 重新构建项目

### 添加新的Template

1. 在`src/main/java/com/h2tg/ysogate/template/`目录下创建对应中间件的子目录
2. 创建新的template类
3. 重新构建项目

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

## 安全使用说明

⚠️ **重要提醒**：本工具仅面向**合法授权**的安全测试使用，请勿用于非法目的。

使用本工具进行安全测试时，请确保：
1. 已获得目标系统的明确授权
2. 仅在授权范围内进行测试
3. 遵守相关法律法规

## 免责声明

本项目仅面向安全研究与学习，禁止任何非法用途。

如您在使用本项目的过程中存在任何非法行为，您需自行承担相应后果。

除非您已充分阅读、完全理解并接受本协议，否则，请您不要使用本项目。

## 参考项目

- https://github.com/frohoff/ysoserial
- https://github.com/X1r0z/JNDIMap
- https://tttang.com/archive/1405/