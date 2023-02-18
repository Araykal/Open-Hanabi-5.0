package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventMotion
import net.minecraft.network.play.server.S03PacketTimeUpdate

class TimeChanger(name: String) : Module(name, ModuleCategory.Render) {
    var time = NumberValue("Time", "ChangeTheWorldTime", 14000L, 0L, 24000L, 100L)

    init {
        addValues(time)
    }

    @EventTarget
    fun onUpdate(e: EventMotion) {
        if (mc.theWorld != null) mc.theWorld.worldTime = time.value
    }

    @EventTarget
    fun onRec(event: EventPacketReceived) {
        if (event.packet is S03PacketTimeUpdate) event.cancel()
    }
}