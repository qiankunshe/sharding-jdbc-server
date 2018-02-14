package io.shardingjdbc.server.packet.command;

import io.shardingjdbc.server.constant.StatusFlag;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSentPacket;
import io.shardingjdbc.server.packet.ok.EofPacket;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * COM_QUERY command packet.
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query.html">COM_QUERY</a>
 *
 * @author zhangliang
 */
public final class ComQueryPacket extends CommandPacket {
    
    private String sql;
    
    @Override
    public ComQueryPacket read(final MySQLPacketPayload mysqlPacketPayload) {
        sql = mysqlPacketPayload.readStringEOF();
        return this;
    }
    
    // TODO connect sj
    @Override
    public List<MySQLSentPacket> execute() {
        List<MySQLSentPacket> result = new LinkedList<>();
        int currentSequenceId = getSequenceId();
        result.add(new ComQueryResponsePacket(++currentSequenceId, 1));
        for (int i = 0; i < 1; i++) {
            result.add(new ColumnDefinition41Packet(++currentSequenceId, "schema", "table", "", "name", "", 65535, ColumnType.MYSQL_TYPE_STRING, 0));
        }
        result.add(new EofPacket(++currentSequenceId, 0, StatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue()));
        for (int i = 0; i < 1; i++) {
            result.add(new ResultSetRowPacket(++currentSequenceId, Collections.singletonList("Sharding JDBC")));
        }
        result.add(new EofPacket(++currentSequenceId, 0, StatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue()));
        return result;
    }
}
