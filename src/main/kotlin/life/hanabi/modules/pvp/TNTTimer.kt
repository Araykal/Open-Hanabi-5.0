package life.hanabi.modules.pvp

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.ColorValue
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.item.EntityTNTPrimed
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat

class TNTTimer(name: String) : Module(name, ModuleCategory.PVP) {

    companion object {
        var color = ColorValue("Color", "Font color", Color(0, 0, 0, 80).rgb)
        var bgColor = ColorValue("BackgroundColor", "Background color", Color(0, 0, 0, 80).rgb)

        fun doRender(entity: EntityTNTPrimed) {
            GL11.glPushMatrix()
            GL11.glEnable(3042)
            GL11.glDisable(2929)
            GL11.glNormal3f(0f, 1f, 0f)
            GlStateManager.enableBlend()
            GL11.glBlendFunc(770, 771)
            GL11.glDisable(3553)
            val partialTicks = mc.timer.renderPartialTicks
            val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks.toDouble() - RenderManager.renderPosX
            val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks.toDouble() - RenderManager.renderPosY
            val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks.toDouble() - RenderManager.renderPosZ
            var scale = 0.065f

            GlStateManager.translate(x, y + entity.height + 0.5f - entity.height / 2f, z)

            GL11.glNormal3f(0f, 1f, 0f)
            GlStateManager.rotate(-mc.renderManager.playerViewY, 0f, 1f, 0f)
            GL11.glScalef((-2f).let { scale /= it; scale }, -scale, -scale)
            val xLeft = -10.0
            val xRight = 10.0
            val yUp = -20.0
            val yDown = -10.0
            drawRect(xLeft.toFloat(), yUp.toFloat(), xRight.toFloat(), yDown.toFloat(), bgColor.color)
            drawTime(entity)
            GL11.glEnable(3553)
            GL11.glEnable(2929)
            GlStateManager.disableBlend()
            GL11.glDisable(3042)
            GL11.glColor4f(1f, 1f, 1f, 1f)
            GL11.glNormal3f(1f, 1f, 1f)
            GL11.glPopMatrix()
        }

        private fun getWidth(text: String): Int {
            return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text)
        }

        private fun drawTime(entity: EntityTNTPrimed) {
            val width = getWidth("0f") / 2f + 6f
            GlStateManager.disableDepth()
            val df = DecimalFormat("0f")
            mc.fontRendererObj.drawStringWithShadow(df.format(entity.fuse / 20.0), -width + 5.5f, -20f, color.color)
            GlStateManager.enableDepth()
        }

        fun drawRect(g: Float, h: Float, i: Float, j: Float, col1: Int) {
            val f = (col1 shr 24 and 0xFF).toFloat() / 255f
            val f1 = (col1 shr 16 and 0xFF).toFloat() / 255f
            val f2 = (col1 shr 8 and 0xFF).toFloat() / 255f
            val f3 = (col1 and 0xFF).toFloat() / 255f
            GL11.glEnable(3042)
            GL11.glDisable(3553)
            GL11.glBlendFunc(770, 771)
            GL11.glEnable(2848)
            GL11.glPushMatrix()
            GL11.glColor4f(f1, f2, f3, f)
            GL11.glBegin(7)
            GL11.glVertex2d(i.toDouble(), h.toDouble())
            GL11.glVertex2d(g.toDouble(), h.toDouble())
            GL11.glVertex2d(g.toDouble(), j.toDouble())
            GL11.glVertex2d(i.toDouble(), j.toDouble())
            GL11.glEnd()
            GL11.glPopMatrix()
            GL11.glEnable(3553)
            GL11.glDisable(3042)
            GL11.glDisable(2848)
        }
    }
}