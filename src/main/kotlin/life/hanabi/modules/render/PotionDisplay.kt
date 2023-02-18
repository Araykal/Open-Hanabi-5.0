package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.utils.render.BlurBuffer
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import java.awt.Color

class PotionDisplay(name: String) : Module(name, ModuleCategory.Render) {
    var background = BooleanValue("Background", "Background", false)
    var blur = BooleanValue("Blur", "Blur", false)
    var color = ColorValue("BackgroundColor", "Background color", Color(0, 0, 0, 80).rgb)

    init {
        addValues(background, blur, color)
    }

    override fun onGui() {
        super.onGui()
        val sr = ScaledResolution(mc)
        val i = x * sr.scaledWidth
        var j = y * sr.scaledHeight
        width = 166f
        val collection: Collection<PotionEffect> = mc.thePlayer.activePotionEffects
        if (collection.isNotEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            GlStateManager.disableLighting()
            var l = 33
            if (collection.size > 5) {
                l = 132 / (collection.size - 1)
            }
            for (potioneffect in mc.thePlayer.activePotionEffects) {
                if (blur.value) {
                    BlurBuffer.blurArea(i + 1, j + 1, i + 164, j + l - 1, true)
                }
                if (background.value) {
                    RenderUtil.drawRect(i + 1, j + 1, i + 164, j + l - 1, color.color)
                }
                val potion = Potion.potionTypes[potioneffect.potionID]
                GlStateManager.enableBlend()
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                mc.textureManager.bindTexture(ResourceLocation("textures/gui/container/inventory.png"))
                if (potion.hasStatusIcon()) {
                    val i1 = potion.statusIconIndex
                    mc.ingameGUI.drawTexturedModalRect(i + 6, j + 7, i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18)
                }
                var s1 = I18n.format(potion.name)
                if (potioneffect.amplifier == 1) {
                    s1 = s1 + " " + I18n.format("enchantment.level.2")
                } else if (potioneffect.amplifier == 2) {
                    s1 = s1 + " " + I18n.format("enchantment.level.3")
                } else if (potioneffect.amplifier == 3) {
                    s1 = s1 + " " + I18n.format("enchantment.level.4")
                }
                mc.fontRendererObj.drawStringWithShadow(s1, i + 10 + 18, j + 6, 16777215)
                val s = Potion.getDurationString(potioneffect)
                mc.fontRendererObj.drawStringWithShadow(s, i + 10 + 18, j + 6 + 10, 8355711)
                j += l.toFloat()
            }
            height = j - y * sr.scaledHeight
        }
    }
}