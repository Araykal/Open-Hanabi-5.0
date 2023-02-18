package life.hanabi.modules.pvp

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue

class CustomFov(name: String) : Module(name, ModuleCategory.PVP) {
    init {
        addValues(noBowFov, noSpeedFov)
    }

    companion object {
        @JvmField
        var noSpeedFov = BooleanValue("NoSpeedFov", "Remove fov change of speed effects", true)
        @JvmField
        var noBowFov = BooleanValue("NoBowFov", "Remove fov change of using bows", true)
    }
}