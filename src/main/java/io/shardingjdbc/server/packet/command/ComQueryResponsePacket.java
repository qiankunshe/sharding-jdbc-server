package io.shardingjdbc.server.packet.command;

import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSentPacket;

/**
 * COM_QUERY response packet.
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html">COM_QUERY response</a>
 *
 * @author zhangliang
 */
public final class ComQueryResponsePacket extends MySQLSentPacket {
    
    @Override
    public void write(final MySQLPacketPayload mysqlPacketPayload) {
        
    }
}
