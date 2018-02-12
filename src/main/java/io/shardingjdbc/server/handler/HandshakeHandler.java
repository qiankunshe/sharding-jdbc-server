package io.shardingjdbc.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.handshake.AuthPluginData;
import io.shardingjdbc.server.packet.handshake.ConnectionIdGenerator;
import io.shardingjdbc.server.packet.handshake.HandshakePacket;
import io.shardingjdbc.server.packet.handshake.HandshakeResponse41Packet;
import io.shardingjdbc.server.packet.ok.OKPacket;

/**
 * Handshake handler.
 * 
 * @author zhangliang 
 */
public class HandshakeHandler extends ChannelInboundHandlerAdapter {
    
    private AuthPluginData authPluginData;
    
    @Override
    public void channelActive(final ChannelHandlerContext context) throws Exception {
        authPluginData = new AuthPluginData();
        context.writeAndFlush(new HandshakePacket(ConnectionIdGenerator.getInstance().nextId(), authPluginData));
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) throws Exception {
        HandshakeResponse41Packet response41 = new HandshakeResponse41Packet().read(new MySQLPacketPayload((ByteBuf) message));
        // TODO use authPluginData to auth
        
        context.writeAndFlush(new OKPacket(1, 0L, 0L, 0, 0, "connection success"));
    }
}
