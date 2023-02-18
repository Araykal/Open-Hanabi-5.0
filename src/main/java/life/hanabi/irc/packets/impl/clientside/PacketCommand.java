package life.hanabi.irc.packets.impl.clientside;

import life.hanabi.irc.packets.Packet;

public class PacketCommand extends Packet {
    public String[] command;

    public PacketCommand(String[] command) {
        super(Type.COMMAND);
        this.command = command;
    }
}
