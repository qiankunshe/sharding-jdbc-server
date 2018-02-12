此项目是sharding-jdbc的server版本, 用Netty实现MySQL protocol, 然后在使用sharding jdbc进行分库分表. 

当项目开发出现技术问题时, 此reop用于技术难点的攻克.

使用方法:
1, 安装mysql命令行客户端
2. 启动项目, 启动类: io.shardingjdbc.server.Bootstrap
3. 执行命令 mysql -u root -h127.0.0.1 -P3307, 即可进入在项目中debug

目前进行mysql的连接创建阶段. 大致步骤流程:

1. 由客户端连接sharding-jdbc-server, sharding-jdbc-server根据mysql protocol发送handshake packet至客户端. (sharding-jdbc-server负责生成handshake packet并发送至客户端)
2. mysql客户端返回handshakeResponse41 packet, 由sharding-jdbc-server解析. (sharding-jdbc-server负责读取handshakeResponse41 packet)
3. sharding-jdbc发送ok协议至mysql客户端 (sharding-jdbc-server生成ok packet,并发送至客户端)

目前问题:

1. 将ok packet发送至客户端时, MySQLMessageCodec中的decode方法会调用,这里比较奇怪. decode应该是读取数据调用,不应是发送数据时调用吧
2. 在decode方法中的bytebuf进行任何操作都会报错,异常信息: 

```
io.netty.util.IllegalReferenceCountException: refCnt: 0, decrement: 1
```

3. 关键点在于HandshakeHandler的channelRead方法中的下面代码, 将OKPacket发送至客户端,却被MySQLMessageCodec中的decode方法拦截,并报错:

```java
context.writeAndFlush(new OKPacket(1, 0L, 0L, 0, 0, "connection success"));
```

如果有对netty熟悉的朋友请赐教.

PS: 希望社区的朋友踊跃参与, 提交记录最终会同步至sharding jdbc的官方github repo中. 感谢关注.
