package life.hanabi.modules.misc

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketSend
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.utils.NetworkUtils
import life.hanabi.utils.PlayerUtil
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C0CPacketInput

class Patcher(name: String) : Module(name, ModuleCategory.Misc) {
    @EventTarget
    fun onUpdate(event: EventMotion) {
        if (mc.thePlayer.ticksExisted % 5 == 0) NetworkUtils.sendPacketNoEvent(C0CPacketInput())
    }

    @EventTarget
    fun onSend(event: EventPacketSend) {
        if (event.getPacket() is C05PacketPlayerLook || event.getPacket() is C06PacketPlayerPosLook) {
            if (mc.currentScreen is GuiDownloadTerrain) mc.currentScreen = null
            if (mc.thePlayer.ticksExisted < 100) event.isCancelled = true

            if (mc.thePlayer.ticksExisted % 100 == 0) {
                event.isCancelled = true
           //     PlayerUtil.tellPlayerWithPrefix("Balls")
            }
        }
    }
}