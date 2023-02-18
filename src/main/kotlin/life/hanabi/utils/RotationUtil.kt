package life.hanabi.utils

import net.minecraft.client.Minecraft
import net.minecraft.util.MathHelper

fun getRotationFromPosition(x: Double, z: Double, y: Double): FloatArray? {
    val xDiff = x - Minecraft.getMinecraft().thePlayer.posX
    val zDiff = z - Minecraft.getMinecraft().thePlayer.posZ
    val yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2
    val dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff).toDouble()
    val yaw = (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI).toFloat() - 90.0f
    val pitch = (-Math.atan2(yDiff, dist) * 180.0 / Math.PI).toFloat()
    return floatArrayOf(yaw, pitch)
}