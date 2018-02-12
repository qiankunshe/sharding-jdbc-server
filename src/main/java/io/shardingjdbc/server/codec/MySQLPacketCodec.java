package io.shardingjdbc.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.shardingjdbc.server.packet.MySQLPacket;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSendPacket;

import java.util.List;

/**
 * MySQL packet codec.
 * 
 * @author zhangliang 
 */
public class MySQLPacketCodec extends ByteToMessageCodec<MySQLSendPacket> {
    
    @Override
    protected void decode(final ChannelHandlerContext context, final ByteBuf in, final List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes < MySQLPacket.PAYLOAD_LENGTH || readableBytes < readPayloadLength(in)) {
            return;
        }
        out.add(in);
    }
    
    private int readPayloadLength(final ByteBuf payload) {
        return payload.readMediumLE();
    }
    
    @Override
    protected void encode(final ChannelHandlerContext context, final MySQLSendPacket mysqlSendPacket, final ByteBuf out) throws Exception {
        MySQLPacketPayload mysqlPacketPayload = new MySQLPacketPayload(context.alloc().buffer());
        writeMySQLPacket(mysqlSendPacket, mysqlPacketPayload, out);
        context.writeAndFlush(out);
    }
    
    private void writeMySQLPacket(final MySQLSendPacket mysqlSendPacket, final MySQLPacketPayload mysqlPacketPayload, final ByteBuf out) {
        mysqlSendPacket.write(mysqlPacketPayload);
        out.writeMediumLE(mysqlPacketPayload.getByteBuf().readableBytes());
        out.writeByte(mysqlSendPacket.getSequenceId());
        out.writeBytes(mysqlPacketPayload.getByteBuf());
    }
}
