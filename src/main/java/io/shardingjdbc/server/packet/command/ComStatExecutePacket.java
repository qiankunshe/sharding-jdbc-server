package io.shardingjdbc.server.packet.command;

import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSentPacket;
import io.shardingjdbc.server.packet.ok.ErrPacket;

/**
 * COM_STMT_EXECUTE command packet.
 *
 * @author zhangliang
 */
public final class ComStatExecutePacket extends CommandPacket {
    
    @Override
    public ComStatExecutePacket read(final MySQLPacketPayload mysqlPacketPayload) {
        return this;
    }
    
    @Override
    public MySQLSentPacket execute() {
        return new ErrPacket(getSequenceId() + 1, 1 ,"x", "xxxxx", "xxxxxxx");
    }
}
