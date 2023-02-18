package life.hanabi.irc.packets.impl.clientside;

import life.hanabi.irc.management.User;
import life.hanabi.irc.packets.Packet;

public class PacketLogin extends Packet {
    public User user;
    public String version;


    public PacketLogin(String username, String password, String hwid, String text) {
        super(Type.LOGIN);
        user = new User(username, password, hwid, text);
    }
}
