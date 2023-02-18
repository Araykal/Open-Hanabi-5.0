package life.hanabi.irc.packets.impl;

import life.hanabi.irc.packets.Packet;

public class PacketMessage extends Packet {
    public PacketMessage(String content) {
        super(Type.MESSAGE, content);
    }
}
