package io.shardingjdbc.server.packet.command;

import io.shardingjdbc.server.packet.MySQLReceivedPacket;
import io.shardingjdbc.server.packet.MySQLSentPacket;

/**
 * Command packet.
 *
 * @author zhangliang
 */
public abstract class CommandPacket extends MySQLReceivedPacket {
    
    /**
     * Execute command.
     * 
     * @return result packet to be sent
     */
    public abstract MySQLSentPacket execute();
}
