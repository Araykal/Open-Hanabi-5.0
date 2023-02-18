package life.hanabi.irc.packets.impl.serverside;

import life.hanabi.irc.packets.Packet;

public class PacketGetRep extends Packet {
    public String id;
    public PacketGetRep(String id, String content) {
        super(Type.RETURN, content);
        this.id = id;
    }
}
