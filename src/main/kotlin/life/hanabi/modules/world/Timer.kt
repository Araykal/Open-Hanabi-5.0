package life.hanabi.modules.world

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.NumberValue

class Timer(name: String) : Module(name, ModuleCategory.World) {
    var timer = NumberValue("Timer", "timer", 1.2f, 0f, 10f, 0.1f)

    init {
        addValues(timer)
    }

    override fun onEnable() {
        super.onEnable()
        mc.timer.timerSpeed = timer.value
    }

    override fun onDisable() {
        super.onDisable()
        mc.timer.timerSpeed = 1f
    }
}