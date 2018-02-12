package io.shardingjdbc.server.packet.handshake;

import io.shardingjdbc.server.constant.CapabilityFlag;
import io.shardingjdbc.server.constant.ServerInfo;
import io.shardingjdbc.server.constant.StatusFlag;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLSendPacket;
import lombok.Getter;

/**
 * Handshake packet protocol.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake">Handshake</a>
 * 
 * @author zhangliang
 */
@Getter
public class HandshakePacket extends MySQLSendPacket {
    
    private final int connectionId;
    
    private final AuthPluginData authPluginData;
    
    public HandshakePacket(final int connectionId, AuthPluginData authPluginData) {
        setSequenceId((byte) 0);
        this.connectionId = connectionId;
        this.authPluginData = authPluginData;
    }
    
    @Override
    public void write(final MySQLPacketPayload mysqlPacketPayload) {
        mysqlPacketPayload.writeInt1(ServerInfo.PROTOCOL_VERSION);
        mysqlPacketPayload.writeStringNul(ServerInfo.SERVER_VERSION);
        mysqlPacketPayload.writeInt4(connectionId);
        mysqlPacketPayload.writeStringNul(new String(authPluginData.getAuthPluginDataPart1()));
        mysqlPacketPayload.writeInt2(CapabilityFlag.calculateHandshakeCapabilityFlagsLower());
        mysqlPacketPayload.writeInt1(ServerInfo.CHARSET);
        mysqlPacketPayload.writeInt2(StatusFlag.calculateHandshakeStatusFlags());
        mysqlPacketPayload.writeInt2(CapabilityFlag.calculateHandshakeCapabilityFlagsUpper());
        mysqlPacketPayload.writeInt1(0);
        mysqlPacketPayload.writeReserved(10);
        mysqlPacketPayload.writeStringNul(new String(authPluginData.getAuthPluginDataPart2()));
    }
}
