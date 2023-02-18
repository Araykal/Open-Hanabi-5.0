package life.hanabi.modules.render

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11

class ArmorStatus(name: String) : Module(name, ModuleCategory.Render) {
    var damageValue = BooleanValue("Damage", "Display damage", false)

    init {
        super.addValues(damageValue)
    }

    override fun onGui() {
        super.onGui()
        val sr = ScaledResolution(mc)
        var y1 = 0
        var wd = 0
        val armorInventory = mc.thePlayer.inventory.armorInventory
        if (armorInventory[3] != null) {
            val temp = draw(armorInventory[3], (x * sr.scaledWidth).toDouble(), (y * sr.scaledHeight).toDouble())
            if (wd < temp) wd = temp
            y1 += 15
        }
        if (armorInventory[2] != null) {
            val temp = draw(armorInventory[2], (x * sr.scaledWidth).toDouble(), (y * sr.scaledHeight + y1).toDouble())
            if (wd < temp) wd = temp
            y1 += 15
        }
        if (armorInventory[1] != null) {
            val temp = draw(armorInventory[1], (x * sr.scaledWidth).toDouble(), (y * sr.scaledHeight + y1).toDouble())
            if (wd < temp) wd = temp
            y1 += 15
        }
        if (armorInventory[0] != null) {
            val temp = draw(armorInventory[0], (x * sr.scaledWidth).toDouble(), (y * sr.scaledHeight + y1).toDouble())
            if (wd < temp) wd = temp
            y1 += 15
        }
        if (mc.thePlayer.heldItem != null) {
            val temp = draw(mc.thePlayer.heldItem, (x * sr.scaledWidth).toDouble(), (y * sr.scaledHeight + y1).toDouble())
            if (wd < temp) wd = temp
            y1 += 15
        }
        width = Math.max(10 + Hanabi.INSTANCE.fontLoaders.syFont18.getStringWidth(name), 15 + wd).toFloat()
        height = y1.toFloat()
    }

    fun draw(item: ItemStack?, x: Double, y: Double): Int {
        var temp = 0
        if (item == null) return 0
        GL11.glPushMatrix()
        val ir = mc.renderItem
        RenderHelper.enableGUIStandardItemLighting()
        ir.renderItemIntoGUI(item, x.toInt(), y.toInt())
        ir.renderItemOverlays(mc.fontRendererObj, item, x.toInt(), y.toInt())
        RenderHelper.disableStandardItemLighting()
        val damage = item.maxDamage - item.itemDamage
        GlStateManager.enableAlpha()
        GlStateManager.disableCull()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.clear(256)
        if (damageValue.value && damage > 0) {
            Hanabi.INSTANCE.fontLoaders.arial16.drawStringWithShadow(damage.toString() + "/" + item.maxDamage, (x + 15 + 2).toFloat(), (y + 6).toInt().toFloat(), -1)
            temp = Hanabi.INSTANCE.fontLoaders.arial16.getStringWidth(damage.toString() + "/" + item.maxDamage)
        }
        GL11.glPopMatrix()
        return temp
    }
}