package life.hanabi.modules.pvp

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.ColorValue
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

class Coordinates(name: String) : Module(name, ModuleCategory.PVP) {
    var color = ColorValue("Color", "Font color", Color(255, 255, 255).rgb)

    init {
        addValues(color)
    }

    override fun onGui() {
        super.onGui()
        val sr = ScaledResolution(mc)
        val c = "X:" + mc.thePlayer.posX + " Y:" + mc.thePlayer.posY + " Z:" + mc.thePlayer.posZ

        mc.fontRendererObj.drawStringWithShadow(c, x * sr.scaledWidth, y * sr.scaledHeight, color.color)
        width = mc.fontRendererObj.getStringWidth(c).toFloat()
        height = 10f
    }
}