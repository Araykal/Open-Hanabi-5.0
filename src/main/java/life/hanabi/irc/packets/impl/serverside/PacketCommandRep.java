package life.hanabi.irc.packets.impl.serverside;

import life.hanabi.irc.packets.Packet;

public class PacketCommandRep extends Packet {
    public String rep;

    public PacketCommandRep(String command) {
        super(Type.COMMAND_REP);
        this.rep = command;
    }
}
