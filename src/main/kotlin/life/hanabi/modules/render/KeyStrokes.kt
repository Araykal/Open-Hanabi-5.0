package life.hanabi.modules.render

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.core.values.values.NumberValue
import java.awt.Color

class KeyStrokes(name: String) : Module(name, ModuleCategory.PVP) {
    @JvmField
    var showWASD = BooleanValue("ShowWASD", false)
    @JvmField
    var arrowKeys = BooleanValue("ArrowKeys", false)
    @JvmField
    var showMouseButtons = BooleanValue("ShowMouseButtons", false)
    @JvmField
    var showCPS = BooleanValue("ShowCPS", false)
    @JvmField
    var showCPSOnButtons = BooleanValue("ShowCPSOnButtons", false)
    @JvmField
    var showSpacebar = BooleanValue("ShowSpacebar", false)
    @JvmField
    var showSneak = BooleanValue("ShowSneak", false)
    @JvmField
    var showFPS = BooleanValue("ShowFPS", false)
    @JvmField
    var showPing = BooleanValue("ShowPing", false)
    @JvmField
    var chroma = BooleanValue("Chroma", true)
    @JvmField
    var keyBackground = BooleanValue("KeyBackground", true)
    @JvmField
    var fadeTime = NumberValue("FadeTime", 1, 0.1, 10, 0.1)
    @JvmField
    var color = ColorValue("Color", Color(255, 255, 255).rgb)
    @JvmField
    var pressedColor = ColorValue("PressedColor", Color(0, 0, 0).rgb)
    @JvmField
    var backgroundColor = ColorValue("BackgroundColor", Color(0, 0, 0, 80).rgb)
    @JvmField
    var backgroundPressedColor = ColorValue("BackgroundPressedColor", Color(255, 255, 255, 100).rgb)

    init {
        addValues(showWASD, arrowKeys, showMouseButtons, showCPS, showCPSOnButtons, showSpacebar, showSneak, showFPS, showPing, chroma, keyBackground, fadeTime, color, pressedColor, backgroundColor, backgroundPressedColor)
    }

    override fun onEnable() {
        super.onEnable()
    }

    override fun onGui() {
        super.onGui()
        width = 74f
        height = 50f
        if (showCPS.value || showSneak.value || showFPS.value) {
            height += 24f
        }
        if (showMouseButtons.value) {
            height += 24f
        }
        if (showWASD.value) {
            height += 48f
        }
        if (!showFPS.value) {
            height -= 18f
        }
        if (!showSpacebar.value) {
            height -= 18f
        }
        if (!showCPS.value) {
            height -= 18f
        }
        if (showCPSOnButtons.value) {
            height -= 18f
        }
        if (!showPing.value) {
            height -= 18f
        }
        Hanabi.INSTANCE.guiCustom.keyStrokes.renderer.renderKeystrokes()
    }
}