package life.hanabi.utils

import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11


fun drawTracer(position: Vec3, color: Int) {
    val posX =
        position.xCoord - RenderManager.renderPosX
    val posY =
        position.yCoord - RenderManager.renderPosY
    val posZ =
        position.zCoord - RenderManager.renderPosZ

    val mc = Minecraft.getMinecraft()
    val old = mc.gameSettings.viewBobbing
    RenderUtil.startDrawing()
    mc.gameSettings.viewBobbing = false
    mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2)
    mc.gameSettings.viewBobbing = old
    drawLine(color, posX, posY, posZ)
    RenderUtil.stopDrawing()
}

fun square(`in`: Double): Double {
    return `in` * `in`
}

private fun drawLine(color: Int, x: Double, y: Double, z: Double) {
    val mc = Minecraft.getMinecraft()
    GL11.glEnable(2848)
    GL11.glColor3f(
        (color shr 16 and 255).toFloat() / 255f,
        (color shr 8 and 255).toFloat() / 255f,
        (color and 255).toFloat() / 255f
    )
    GL11.glLineWidth(1.0f)
    GL11.glBegin(1)
    GL11.glVertex3d(0.0, mc.thePlayer.eyeHeight.toDouble(), 0.0)
    GL11.glVertex3d(x, y, z)
    GL11.glEnd()
    GL11.glDisable(2848)
}