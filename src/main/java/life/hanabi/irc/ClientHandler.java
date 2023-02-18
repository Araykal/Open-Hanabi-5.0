package life.hanabi.irc;

import cn.qiriyou.IIiIIiiiIiii;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import life.hanabi.Hanabi;
import life.hanabi.gui.impl.GuiLogin;
import life.hanabi.irc.packets.Packet;
import life.hanabi.irc.packets.impl.PacketMessage;
import life.hanabi.irc.packets.impl.clientside.PacketHeartBeat;
import life.hanabi.irc.packets.impl.serverside.PacketGetRep;
import life.hanabi.irc.packets.impl.serverside.PacketRegisterRep;
import life.hanabi.irc.packets.impl.serverside.PacketServerRep;
import life.hanabi.irc.utils.PacketUtil;
import life.hanabi.utils.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
@IIiIIiiiIiii
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelHandlerContext context;
    private int rec;
    public static long currentTime = System.currentTimeMillis();


    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        Hanabi.INSTANCE.loggedIn = false;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        System.out.println("Disconnected from server");
        if (rec < 30) {
            Hanabi.INSTANCE.loggedIn = false;
            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiLogin)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiLogin());
            }
            Hanabi.INSTANCE.client.reconnect();
            rec++;
        } else {
            Minecraft.getMinecraft().thePlayer = null;
            Minecraft.getMinecraft().thePlayer.jump();
            Hanabi.INSTANCE.moduleManager = null;
            Hanabi.INSTANCE = null;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("Connected to server");
        context = ctx;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (System.currentTimeMillis() - currentTime > 60500 && !(Minecraft.getMinecraft().currentScreen instanceof GuiLogin)) {
                        Minecraft.getMinecraft().thePlayer = null;
                        Minecraft.getMinecraft().thePlayer.jump();
                        Hanabi.INSTANCE.moduleManager = null;
                        Hanabi.INSTANCE = null;
                    }
                    ctx.writeAndFlush(PacketUtil.pack(new PacketHeartBeat(Minecraft.getMinecraft().getSession() != null ? Minecraft.getMinecraft().getSession().getUsername() : "empty")));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        }).start();
    }

    private void sendChatMessage(ChannelHandlerContext ctx, String msg) {
        ctx.writeAndFlush(PacketUtil.pack(new PacketMessage(msg)));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Packet p = PacketUtil.unpack(s, Packet.class);
        if (p != null) {
            switch (p.type) {
                case LOGINREP:
                    PacketServerRep packetLogin = PacketUtil.unpack(s, PacketServerRep.class);
                    Hanabi.INSTANCE.rank = packetLogin.content;
                    Hanabi.INSTANCE.loggedIn = true;
                    Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
                    Hanabi.INSTANCE.client.flag = 1;
                    break;
                case MESSAGE:
                    if (p.content.equals("KICKUSER")) {
                        Minecraft.getMinecraft().shutdown();
                    }
                    if (Minecraft.getMinecraft().thePlayer != null) {
                        PlayerUtil.tellPlayerWithoutPrefix(p.content);
                    }
                    break;
                case RETURN:
                    PacketGetRep packetReturn = PacketUtil.unpack(s, PacketGetRep.class);
                    PlayerUtil.tellPlayerWithoutPrefix(packetReturn.content);
                    break;
                case REGISTERREP:
                    PacketRegisterRep packetRegisterRep = PacketUtil.unpack(s, PacketRegisterRep.class);
                    break;
                case HEARTBEATREP:
                    currentTime = System.currentTimeMillis();
                    break;
            }
        }
    }
}