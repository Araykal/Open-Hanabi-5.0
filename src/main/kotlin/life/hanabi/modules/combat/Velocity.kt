package life.hanabi.modules.combat

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.gui.notification.Info
import life.hanabi.gui.notification.Notification
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

class Velocity(name: String) : Module(name, ModuleCategory.Combat) {
    var check = BooleanValue("Check", "check", true)

    init {
        addValues(check)
    }

    @EventTarget
    fun onPacket(e: EventPacketReceived) {
        if (e.packet != null && e.packet is S12PacketEntityVelocity) {
            val packet = e.packet as S12PacketEntityVelocity
            if (packet.entityID != mc.thePlayer.entityId) return
        }
        if (e.packet != null && e.packet is S12PacketEntityVelocity || e.packet is S27PacketExplosion)
            if (mc.thePlayer.ticksExisted > 60 && mc.thePlayer.hurtTime == 0 && check.value) {
            Hanabi.INSTANCE.notificationsManager.add(Info("You are possibly getting kb checked", Notification.Type.Info))
        } else {
            e.cancel()
        }
    }
}