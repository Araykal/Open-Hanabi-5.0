package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

class Crosshair(name: String) : Module(name, ModuleCategory.Render) {
    init {
        addValues(dynamic, gap, Companion.width, size, color)
    }

    companion object {
        private val dynamic = BooleanValue("dynamic", "dynamic", true)
        private val gap = NumberValue("gap", "gap", 5.0, 0.25, 15.0, 0.25)
        private val width = NumberValue("width", "width", 1.0, 0.25, 10.0, 0.25)
        private val size = NumberValue("size", "size", 7.0, 0.25, 15.0, 0.25)
        var color = ColorValue("Color", "Crosshair color", Color(255, 255, 255).rgb)
        var boardColor = ColorValue("BoardColor", "Board color", Color(0, 0, 0, 50).rgb)
        fun render() {
            val gap = gap.value
            val width = width.value
            val size = size.value
            val scaledRes = ScaledResolution(mc)
            RenderUtil.rectangleBordered(scaledRes.scaledWidth.toDouble() / 2 - width, scaledRes.scaledHeight.toDouble() / 2 - gap - size - if (isMoving) 2 else 0, scaledRes.scaledWidth.toDouble() / 2 + 1.0f + width, scaledRes.scaledHeight.toDouble() / 2 - gap - if (isMoving) 2 else 0, 0.5, color.color, boardColor.color)
            RenderUtil.rectangleBordered(scaledRes.scaledWidth.toDouble() / 2 - width, scaledRes.scaledHeight.toDouble() / 2 + gap + 1 + (if (isMoving) 2 else 0) - 0.15, scaledRes.scaledWidth.toDouble() / 2 + 1.0f + width, scaledRes.scaledHeight.toDouble() / 2 + 1 + gap + size + (if (isMoving) 2 else 0) - 0.15, 0.5, color.color, boardColor.color)
            RenderUtil.rectangleBordered(scaledRes.scaledWidth.toDouble() / 2 - gap - size - (if (isMoving) 2 else 0) + 0.15, scaledRes.scaledHeight.toDouble() / 2 - width, scaledRes.scaledWidth.toDouble() / 2 - gap - (if (isMoving) 2 else 0) + 0.15, scaledRes.scaledHeight.toDouble() / 2 + 1.0f + width, 0.5, color.color, boardColor.color)
            RenderUtil.rectangleBordered(scaledRes.scaledWidth.toDouble() / 2 + 1 + gap + if (isMoving) 2 else 0, scaledRes.scaledHeight.toDouble() / 2 - width, scaledRes.scaledWidth.toDouble() / 2 + size + gap + 1 + if (isMoving) 2 else 0, scaledRes.scaledHeight.toDouble() / 2 + 1.0f + width, 0.5, color.color, boardColor.color)
        }

        private val isMoving: Boolean
            get() {
                return dynamic.value && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking && (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f)
            }
    }
}