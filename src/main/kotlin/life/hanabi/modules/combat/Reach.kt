package life.hanabi.modules.combat

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.NumberValue

class Reach(name: String) : Module(name, ModuleCategory.PVP) {

    init {
        addValues(range)
    }

    companion object {
        var range = NumberValue("Range", 3.6, 3.0, 5.0, 0.1)

        fun getReach(): Double {
            return range.value
        }
    }
}