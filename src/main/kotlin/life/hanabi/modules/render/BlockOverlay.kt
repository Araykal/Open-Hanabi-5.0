package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.utils.math.ColorUtils
import life.hanabi.utils.render.RenderUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockStairs
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MovingObjectPosition
import org.lwjgl.opengl.GL11
import java.awt.Color

class BlockOverlay(name: String) : Module(name, ModuleCategory.Render) {
    var color1 = ColorValue("OutlineColor", "Color of outline", Color(255, 255, 255).rgb)
    var color = ColorValue("FillColor", "Color of fill", Color(255, 255, 255, 50).rgb)
    var chroma = BooleanValue("Chroma", "chroma", false)
    var throughBlock = BooleanValue("ThroughBlock", true)

    init {
        addValues(color, color1, outlined, fill, chroma, throughBlock)
    }

    @EventTarget
    fun onRender3D(event: EventRender3D) {
        if (event.type == EventType.PRE) {
            return
        }
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            val pos = mc.objectMouseOver.blockPos
            val block = mc.theWorld.getBlockState(pos).block
            val n = pos.x.toDouble()
            val x = n - RenderManager.renderPosX
            val n2 = pos.y.toDouble()
            val y = n2 - RenderManager.renderPosY
            val n3 = pos.z.toDouble()
            val z = n3 - RenderManager.renderPosZ

            GL11.glPushMatrix()
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GL11.glBlendFunc(770, 771)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(2848)

            if (throughBlock.value) {
                GL11.glDisable(2929)
            }

            GL11.glDepthMask(false)
            val chromaColor = ColorUtils.reAlpha(Color.getHSBColor(System.currentTimeMillis() % 3000 / 3000f, 0.8f, 1f).rgb, color.alpha)

            val fill = if (chroma.value) ColorUtils.intToColor(chromaColor) else ColorUtils.intToColor(color.color)

            GlStateManager.color(fill.red / 255f, fill.green / 255f, fill.blue / 255f, fill.alpha / 255f)

            val minX: Double = if (block is BlockStairs || Block.getIdFromBlock(block) == 134) 0.0 else block.blockBoundsMinX
            val minY: Double = if (block is BlockStairs || Block.getIdFromBlock(block) == 134) 0.0 else block.blockBoundsMinY
            val minZ: Double = if (block is BlockStairs || Block.getIdFromBlock(block) == 134) 0.0 else block.blockBoundsMinZ

            if (Companion.fill.value) {
                RenderUtil.drawBoundingBox(AxisAlignedBB(x + minX - 0.005, y + minY - 0.005, z + minZ - 0.005, x + block.blockBoundsMaxX + 0.005, y + block.blockBoundsMaxY + 0.005, z + block.blockBoundsMaxZ + 0.005))
            }
            val chromaColor2 = ColorUtils.reAlpha(Color.getHSBColor(System.currentTimeMillis() % 3000 / 3000f, 0.8f, 1f).rgb, color1.alpha)
            val outline = if (chroma.value) ColorUtils.intToColor(chromaColor2) else ColorUtils.intToColor(color1.color)
            GlStateManager.color(outline.red / 255f, outline.green / 255f, outline.blue / 255f, color1.alpha)
            GL11.glLineWidth(1f)

            if (outlined.value) {
                RenderUtil.drawOutlinedBoundingBox(AxisAlignedBB(x + minX - 0.005, y + minY - 0.005, z + minZ - 0.005, x + block.blockBoundsMaxX + 0.005, y + block.blockBoundsMaxY + 0.005, z + block.blockBoundsMaxZ + 0.005))
            }

            GL11.glDisable(2848)
            GL11.glEnable(3553)
            if (throughBlock.value) {
                GL11.glEnable(2929)
            }
            GL11.glDepthMask(true)
            GL11.glPopMatrix()
        }
    }

    companion object {
        var outlined = BooleanValue("Outlined", "Outlined.", true)
        var fill = BooleanValue("Fill", "Fill.", false)
    }
}