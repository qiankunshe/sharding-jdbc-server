此项目是sharding-jdbc的server版本, 用Netty实现MySQL protocol, 然后在使用sharding jdbc进行分库分表. 

当项目开发出现技术问题时, 此repo用于技术难点的攻克.

使用方法:
1, 安装mysql命令行客户端
2. 启动项目, 启动类: io.shardingjdbc.server.Bootstrap
3. 执行命令 mysql -u root -h127.0.0.1 -P3307, 即可进入在项目中debug

目前进行mysql的连接创建阶段. 大致步骤流程:

1. 由客户端连接sharding-jdbc-server, sharding-jdbc-server根据mysql protocol发送handshake packet至客户端. (sharding-jdbc-server负责生成handshake packet并发送至客户端)
2. mysql客户端返回handshakeResponse41 packet, 由sharding-jdbc-server解析. (sharding-jdbc-server负责读取handshakeResponse41 packet)
3. sharding-jdbc发送ok协议至mysql客户端 (sharding-jdbc-server生成ok packet,并发送至客户端)

目前问题:

将ok packet发送至客户端时, 会报错, 异常信息: 

```
[WARN ] 01:29:22.411 [nioEventLoopGroup-3-1] io.netty.util.ReferenceCountUtil - Failed to release a message: PooledUnsafeDirectByteBuf(freed)
io.netty.util.IllegalReferenceCountException: refCnt: 0, increment: 1
	at io.netty.buffer.AbstractReferenceCountedByteBuf.release0(AbstractReferenceCountedByteBuf.java:100) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.buffer.AbstractReferenceCountedByteBuf.release(AbstractReferenceCountedByteBuf.java:84) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.ReferenceCountUtil.release(ReferenceCountUtil.java:88) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.ReferenceCountUtil.safeRelease(ReferenceCountUtil.java:113) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.ChannelOutboundBuffer.remove0(ChannelOutboundBuffer.java:293) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.ChannelOutboundBuffer.failFlushed(ChannelOutboundBuffer.java:640) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.closeOutboundBufferForShutdown(AbstractChannel.java:681) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.shutdownOutput(AbstractChannel.java:674) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.flush0(AbstractChannel.java:948) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.AbstractNioChannel$AbstractNioUnsafe.flush0(AbstractNioChannel.java:362) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.flush(AbstractChannel.java:901) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.flush(DefaultChannelPipeline.java:1374) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:776) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush(AbstractChannelHandlerContext.java:768) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.flush(AbstractChannelHandlerContext.java:749) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.ChannelOutboundHandlerAdapter.flush(ChannelOutboundHandlerAdapter.java:115) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeFlush0(AbstractChannelHandlerContext.java:776) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeWriteAndFlush(AbstractChannelHandlerContext.java:802) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:814) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:794) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:831) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.shardingjdbc.server.handler.HandshakeHandler.channelActive(HandshakeHandler.java:26) [classes/:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelActive(AbstractChannelHandlerContext.java:213) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelActive(AbstractChannelHandlerContext.java:199) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelActive(AbstractChannelHandlerContext.java:192) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelActive(DefaultChannelPipeline.java:1400) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelActive(AbstractChannelHandlerContext.java:213) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelActive(AbstractChannelHandlerContext.java:199) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelActive(DefaultChannelPipeline.java:919) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.register0(AbstractChannel.java:518) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.access$200(AbstractChannel.java:423) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe$1.run(AbstractChannel.java:482) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:163) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:404) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:463) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:886) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at java.lang.Thread.run(Thread.java:745) [na:1.7.0_79]
[WARN ] 01:29:22.419 [nioEventLoopGroup-3-1] i.n.c.AbstractChannelHandlerContext - Failed to mark a promise as failure because it has failed already: DefaultChannelPromise@6fc620d9(failure: java.nio.channels.ClosedChannelException), unnotified cause: java.nio.channels.ClosedChannelException
	at io.netty.channel.AbstractChannel$AbstractUnsafe.write(...)(Unknown Source)

io.netty.util.IllegalReferenceCountException: refCnt: 0, increment: 1
	at io.netty.buffer.AbstractReferenceCountedByteBuf.release0(AbstractReferenceCountedByteBuf.java:100) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.buffer.AbstractReferenceCountedByteBuf.release(AbstractReferenceCountedByteBuf.java:84) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.ReferenceCountUtil.release(ReferenceCountUtil.java:88) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannel$AbstractUnsafe.write(AbstractChannel.java:871) ~[netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.write(DefaultChannelPipeline.java:1369) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeWrite0(AbstractChannelHandlerContext.java:738) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeWrite(AbstractChannelHandlerContext.java:730) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:816) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:723) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.handler.codec.MessageToByteEncoder.write(MessageToByteEncoder.java:113) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeWrite0(AbstractChannelHandlerContext.java:738) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeWriteAndFlush(AbstractChannelHandlerContext.java:801) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:814) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:794) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:831) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.shardingjdbc.server.handler.HandshakeHandler.channelRead(HandshakeHandler.java:34) [classes/:na]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1412) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:943) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:141) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:645) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:580) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:497) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:459) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:886) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) [netty-all-4.1.20.Final.jar:4.1.20.Final]
	at java.lang.Thread.run(Thread.java:745) [na:1.7.0_79]
```

目前netty使用的是bio,则不会出现此错误, 可以正常登录. 将Bootstrap中的OioEventLoopGroup和OioServerSocketChannel换为NioEventLoopGroup和NioServerSocketChannel就会报错.(目前的代码是bio,可以正常用)

如果有对netty熟悉的朋友请赐教.

PS: 希望社区的朋友踊跃参与, 提交记录最终会同步至sharding jdbc的官方github repo中. 感谢关注.
