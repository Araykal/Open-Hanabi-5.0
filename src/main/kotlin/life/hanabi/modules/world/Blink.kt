package life.hanabi.modules.world

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketSend
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer

class Blink(name: String) : Module(name, ModuleCategory.World) {

    var list = ArrayList<C03PacketPlayer>()

    @EventTarget
    fun onPacket(packet: EventPacketSend) {
        if (packet.packet is C03PacketPlayer) {
            list.add(packet.packet as C03PacketPlayer)
        }
    }

    override fun onEnable() {
        super.onEnable()
        list.clear()
        // add a fake player entity to the world
        val ent = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        val yaw = mc.thePlayer.rotationYaw
        val pitch = mc.thePlayer.rotationPitch
        ent.inventory = mc.thePlayer.inventory
        ent.inventoryContainer = mc.thePlayer.inventoryContainer
        ent.setPositionAndRotation(x, y, z, yaw, pitch)
        ent.rotationYawHead = mc.thePlayer.rotationYawHead
        mc.theWorld.addEntityToWorld(-69, ent)
    }

    override fun onDisable() {
        super.onDisable()
        for (packet in list) {
            mc.netHandler.addToSendQueue(packet)
        }
        mc.theWorld.removeEntityFromWorld(-69)
    }

}