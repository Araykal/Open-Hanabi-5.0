package life.hanabi.modules.world

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.utils.math.TimerUtil

class FastPlace(name: String) : Module(name, ModuleCategory.World) {
    var delay = NumberValue("FastPlace", "Delay", 50.0, 1.0, 500.0, 10.0)
    private val timer = TimerUtil()

    @EventTarget
    fun onUpdate(e: EventMotion) {
        if (e.type == EventType.PRE) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown && timer.hasReached(delay.value)) {
                mc.rightClickDelayTimer = 0
                timer.reset()
            } else {
                mc.rightClickDelayTimer = 1
            }
        }
    }
}