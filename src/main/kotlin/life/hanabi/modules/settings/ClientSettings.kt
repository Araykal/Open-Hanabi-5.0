package life.hanabi.modules.settings

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import java.awt.Color

class ClientSettings(name: String) : Module(name, ModuleCategory.Settings) {
    init {
        addValues(betterInventory, betterButton, chatBackground, screenAnimation, chunkAnimation, chatBackgroundColor)
        canBeEnabled = false
    }

    companion object {
        @JvmField
        var betterInventory = BooleanValue("BetterInventory", "Better Inventory", true)
        @JvmField
        var betterButton = BooleanValue("BetterButton", "BetterButton", true)
        @JvmField
        var chatBackground = BooleanValue("ChatBackground", "ChatBackground", true)
        @JvmField
        var chatBackgroundColor = ColorValue("ChatBackgroundColor", "ChatBackgroundColor", Color(0, 0, 0, 125).rgb)
        @JvmField
        var screenAnimation = BooleanValue("ScreenAnimation", "ScreenAnimation", true)
        @JvmField
        var chunkAnimation = BooleanValue("ChunkAnimation", "ChunkAnimation", true)
    }
}