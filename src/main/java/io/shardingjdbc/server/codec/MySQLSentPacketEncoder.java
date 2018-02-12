package io.shardingjdbc.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSentPacket;

/**
 * MySQL sent packet encoder.
 * 
 * @author zhangliang 
 */
public final class MySQLSentPacketEncoder extends MessageToByteEncoder<MySQLSentPacket> {
    
    @Override
    protected void encode(final ChannelHandlerContext context, final MySQLSentPacket mysqlSentPacket, final ByteBuf out) throws Exception {
        MySQLPacketPayload mysqlPacketPayload = new MySQLPacketPayload(context.alloc().buffer());
        mysqlSentPacket.write(mysqlPacketPayload);
        out.writeMediumLE(mysqlPacketPayload.getByteBuf().readableBytes());
        out.writeByte(mysqlSentPacket.getSequenceId());
        out.writeBytes(mysqlPacketPayload.getByteBuf());
        context.writeAndFlush(out);
    }
}
