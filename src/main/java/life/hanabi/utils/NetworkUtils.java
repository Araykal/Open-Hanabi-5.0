package life.hanabi.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class NetworkUtils extends Utils {
    //judge whether the player is playing on hypixel.net
    public static boolean isOnHypixel() {
        return Minecraft.getMinecraft().theWorld != null && !Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net");
    }

    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().addToSendQueueNoEvent(packet);
    }

    public static void sendPacket(Packet packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

}
