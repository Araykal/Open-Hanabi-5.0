package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory

class FullBright(name: String) : Module(name, ModuleCategory.Render) {
    private var oldGamma = 0f

    override fun onEnable() {
        oldGamma = mc.gameSettings.gammaSetting
        mc.gameSettings.gammaSetting = 15f
        super.onEnable()
    }

    override fun onDisable() {
        mc.gameSettings.gammaSetting = oldGamma
        super.onDisable()
    }
}