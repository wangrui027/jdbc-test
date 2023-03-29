# JDBC 测试程序

## 概述

程序用于测试两个服务器间能否建立 jdbc 连接

## 打包

```bash
sh package.sh
```

## 放置驱动包

程序本身不包含 jdbc 驱动包，请自行准备待测试的驱动包，放入 dist/lib目录下

## 配置修改

dist 目录中包含 jdbc-test.properties 及 jdbc-test.sql，分别用于配置 jdbc 连接信息和待测试 sql 语句，请根据真实测试环境进行配置

## JDBC 测试

linux：

```bash
cd dist
sh run.sh
```

windows：

```bash
cd dist
./run.bat
```

执行 sql 产生的日志文件位于 dist/logs 目录下，每次执行该日志文件内容将被覆盖