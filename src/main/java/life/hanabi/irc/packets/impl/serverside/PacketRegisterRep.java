package life.hanabi.irc.packets.impl.serverside;

import life.hanabi.irc.packets.Packet;

public class PacketRegisterRep extends Packet {

    public boolean success;
    public String key;

    public PacketRegisterRep(String result, boolean success, String key) {
        super(Type.REGISTERREP, result);
        this.success = success;
        this.key = key;
    }
}
