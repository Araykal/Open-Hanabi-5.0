package life.hanabi.modules.movement

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.gui.notification.Info
import life.hanabi.gui.notification.Notification
import life.hanabi.modules.world.AntiFall
import life.hanabi.utils.math.TimerUtil
import net.minecraft.network.play.server.S08PacketPlayerPosLook

class LagBackChecker(name: String) : Module(name, ModuleCategory.Movement) {
    var delay = NumberValue("Delay", "delay", 1000f, 0f, 10000f, 100f)
    var timer = TimerUtil()

    init {
        addValues(delay)
    }

    @EventTarget
    fun onPacket(e: EventPacketReceived) {
        if (e.packet is S08PacketPlayerPosLook) {
            if (timer.delay(delay.value) && !AntiFall.isAboveVoid && mc.thePlayer != null && mc.thePlayer.ticksExisted > 200) {
                Hanabi.INSTANCE.notificationsManager.add(Info("LagBack checked", Notification.Type.Warning))
                timer.reset()
            } else {
                if (ModuleManager.modules["Speed"]!!.stage) {
                    ModuleManager.modules["Speed"]!!.setStage(false)
                }
            }
        }
    }
}