package io.shardingjdbc.server.packet.handshake;

import io.shardingjdbc.server.constant.CapabilityFlag;
import io.shardingjdbc.server.packet.MySQLPacketPayload;
import io.shardingjdbc.server.packet.MySQLReceivedPacket;
import lombok.Getter;

/**
 *
 * Handshake response above MySQL 4.1 packet protocol.
 *
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse41">HandshakeResponse41</a>
 * 
 * @author zhangliang
 */
@Getter
public final class HandshakeResponse41Packet extends MySQLReceivedPacket {
    
    private int capabilityFlags;
    
    private int maxPacketSize;
    
    private byte characterSet;
    
    private String username;
    
    private byte[] authResponse;
    
    private String database;
    
    @Override
    public HandshakeResponse41Packet read(final MySQLPacketPayload mySQLPacketPayload) {
        setSequenceId(mySQLPacketPayload.readInt1());
        capabilityFlags = mySQLPacketPayload.readInt4();
        maxPacketSize = mySQLPacketPayload.readInt4();
        characterSet = (byte) mySQLPacketPayload.readInt1();
        mySQLPacketPayload.skipReserved(23);
        username = mySQLPacketPayload.readStringNul();
        readAuthResponse(mySQLPacketPayload);
        readDatabase(mySQLPacketPayload);
        mySQLPacketPayload.getByteBuf().release();
        return this;
    }
    
    private void readAuthResponse(final MySQLPacketPayload mySQLPacketPayload) {
        if (0 != (capabilityFlags & CapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue())) {
            authResponse = mySQLPacketPayload.readStringLenenc().getBytes();
        } else if (0 != (capabilityFlags & CapabilityFlag.CLIENT_SECURE_CONNECTION.getValue())) {
            int length = mySQLPacketPayload.readInt1();
            authResponse = mySQLPacketPayload.readStringFix(length).getBytes();
        } else {
            authResponse = mySQLPacketPayload.readStringNul().getBytes();
        }
    }
    
    private void readDatabase(final MySQLPacketPayload mySQLPacketPayload) {
        if (0 != (capabilityFlags & CapabilityFlag.CLIENT_CONNECT_WITH_DB.getValue())) {
            database = mySQLPacketPayload.readStringNul();
        }
    }
}
