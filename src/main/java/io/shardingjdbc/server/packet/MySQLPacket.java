package io.shardingjdbc.server.packet;

import lombok.Getter;
import lombok.Setter;

/**
 * MySQL packet.
 *
 * @author zhangliang
 */
@Getter
@Setter
public abstract class MySQLPacket {
    
    private int sequenceId;
}
