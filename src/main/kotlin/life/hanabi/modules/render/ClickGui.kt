package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.gui.clickgui.ClickGui
import life.hanabi.gui.clickui.ClickUIScreen
import org.lwjgl.input.Keyboard

class ClickGui(name: String) :
    Module(name, ModuleCategory.Render) {
    private val pauseGame = BooleanValue("PauseGame", "Pause the game when in single player", true)

    init {
        addValues(pauseGame)
        if (key == 0) key = Keyboard.KEY_RSHIFT
    }

    override fun onEnable() {
        super.onEnable()
        setStage(false)
        if (mc.currentScreen == null) mc.displayGuiScreen(ClickGui(pauseGame.value))
    }

    override fun onDisable() {
        super.onDisable()
    }
}