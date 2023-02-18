package life.hanabi.irc.packets.impl.serverside;

import life.hanabi.irc.packets.Packet;

public class PacketServerRep extends Packet {
    public String userRank;
    public String serverVersion;

    public PacketServerRep(String userRank, String serverVersion, String onlineUsersCount, String content) {
        super(Type.LOGINREP, content);
        this.userRank = userRank;
        this.serverVersion = serverVersion;
    }
}
