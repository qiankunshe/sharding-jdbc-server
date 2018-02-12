package io.shardingjdbc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.shardingjdbc.server.codec.MySQLSentPacketEncoder;
import io.shardingjdbc.server.handler.HandshakeHandler;

/**
 * Sharding-JDBC Server Bootstrap.
 *
 * @author zhangliang
 */
public class Bootstrap {
    
    public static void main(String[] args) throws InterruptedException {
        new Bootstrap().start(3307);
    }
    
    private void start(final int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        
                        @Override
                        public void initChannel(final SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new MySQLSentPacketEncoder());
                            pipeline.addLast(new HandshakeHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
