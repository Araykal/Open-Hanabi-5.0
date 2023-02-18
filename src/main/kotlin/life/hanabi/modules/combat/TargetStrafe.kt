package life.hanabi.modules.combat

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventMove
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.utils.Rotation
import life.hanabi.utils.RotationUtil
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.BlockAir
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.server.S07PacketRespawn
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class TargetStrafe(name: String) : Module(name, ModuleCategory.Combat) {
    var timer = TimerUtil()
    private var strafe = -1
    var yaw = 0f

    init {
        addValues(range, targetkey, voidCheck, behindValue, change, render, hurt)
    }
    @EventTarget
    private fun onChangeWorld(event: EventPacketReceived) {
        if (event.packet is S07PacketRespawn) strafe = -1
    }

    @EventTarget
    private fun onRender3D(event: EventRender3D) {
        if (canStrafe() && render.value) {
            val target = KillAura.target
            if (target != null) {
                esp(target, event.getPartialTicks(), range.value)
            }
        }
    }

    fun esp(entity: Entity, partialTicks: Float, rad: Double) {
        val points = 90f
        GlStateManager.enableDepth()
        var il = 0.0
        while (il < 4.9E-324) {
            GL11.glPushMatrix()
            GL11.glDisable(3553)
            GL11.glEnable(2848)
            GL11.glEnable(2881)
            GL11.glEnable(2832)
            GL11.glEnable(3042)
            GL11.glBlendFunc(770, 771)
            GL11.glHint(3154, 4354)
            GL11.glHint(3155, 4354)
            GL11.glHint(3153, 4354)
            GL11.glDisable(2929)
            GL11.glLineWidth(3.5f)
            GL11.glBegin(3)
            val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
            val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
            val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ
            val pix2 = 6.283185307179586
            val speed = 5000f
            var baseHue = (System.currentTimeMillis() % speed.toInt()).toFloat()

            while (baseHue > speed) {
                baseHue -= speed
            }

            baseHue /= speed
            for (i in 0..90) {
                val max = (i.toFloat() + (il * 8).toFloat()) / points
                var hue = max + baseHue
                while (hue > 1) {
                    hue -= 1f
                }
                val r = 0.003921569f * Color(Color.HSBtoRGB(hue, 0.75f, 1f)).red
                val g = 0.003921569f * Color(Color.HSBtoRGB(hue, 0.75f, 1f)).green
                val b = 0.003921569f * Color(Color.HSBtoRGB(hue, 0.75f, 1f)).blue
                val color = Color.WHITE.rgb
                GL11.glColor3f(r, g, b)
                GL11.glVertex3d(x + rad * cos(i * pix2 / points), y + il, z + rad * sin(i * pix2 / points))
            }
            GL11.glEnd()
            GL11.glDepthMask(true)
            GL11.glEnable(2929)
            GL11.glDisable(2848)
            GL11.glDisable(2881)
            GL11.glEnable(2832)
            GL11.glEnable(3553)
            GL11.glPopMatrix()
            GlStateManager.color(255f, 255f, 255f)
            il += 4.9E-324
        }
    }

    fun isStrafing(event: EventMove, target: EntityLivingBase?, moveSpeed: Double): Boolean {
        if (!canStrafe()) return false
        val pressingSpace = !targetkey.value || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode)
        if (!stage || moveSpeed == 0.0 || !pressingSpace) return true
        var aroundVoid = false
        for (x in -1..0) for (z in -1..0) if (isVoid(x, z)) aroundVoid = true
        var yaw = getRotationFromEyeHasPrev(target).yaw
        val behindTarget = RotationUtil.getRotationDifference(MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(target!!.rotationYaw)) <= 10

        if (mc.thePlayer.isCollidedHorizontally || aroundVoid && voidCheck.value || behindTarget && behindValue.value) strafe *= -1

        var targetStrafe = if (change.value && mc.thePlayer.moveStrafing != 0f) mc.thePlayer.moveStrafing * strafe else strafe.toFloat()

        if (isAboveVoid) targetStrafe = 0f
        val rotAssist = 45f / getEnemyDistance(target).toFloat()
        val moveAssist = 45f / getStrafeDistance(target)
        var mathStrafe = 0f

        if (targetStrafe > 0) {
            if ((target!!.entityBoundingBox.minY > mc.thePlayer.entityBoundingBox.maxY || target.entityBoundingBox.maxY < mc.thePlayer.entityBoundingBox.minY) && getEnemyDistance(target) < range.value) yaw += -rotAssist
            mathStrafe += -moveAssist
        } else if (targetStrafe < 0) {
            if ((target!!.entityBoundingBox.minY > mc.thePlayer.entityBoundingBox.maxY || target.entityBoundingBox.maxY < mc.thePlayer.entityBoundingBox.minY) && getEnemyDistance(target) < range.value) yaw += rotAssist
            mathStrafe += moveAssist
        }
        val doSomeMath = doubleArrayOf(cos(Math.toRadians((yaw + 90.0 + mathStrafe))), sin(Math.toRadians((yaw + 90.0 + mathStrafe))))
        val asLast = doubleArrayOf(moveSpeed * doSomeMath[0], moveSpeed * doSomeMath[1])

        event.x = asLast[0].also { mc.thePlayer.motionX = it }
        event.z = asLast[1].also { mc.thePlayer.motionZ = it }

        return false
    }

    private fun getEnemyDistance(target: EntityLivingBase?): Double {
        return mc.thePlayer.getDistance(target!!.posX, mc.thePlayer.posY, target.posZ)
    }

    private fun getStrafeDistance(target: EntityLivingBase?): Float {
        return Math.max(getEnemyDistance(target) - range.value, getEnemyDistance(target) - (getEnemyDistance(target) - range.value / (range.value * 2f))).toFloat()
    }

    private fun isVoid(x: Int, z: Int): Boolean {
        if (mc.thePlayer.posY < 0) return true
        var off = 0.0
        while (off < mc.thePlayer.posY + 2.0) {
            val bb = mc.thePlayer.entityBoundingBox.offset(x.toDouble(), -off, z.toDouble())
            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
        }
        return true
    }

    companion object {
        // SkidSense
        var range = NumberValue("Strafe Range", "Strafe Range", 2.0, 0.5, 4.5, 0.1)
        var targetkey = BooleanValue("Space Toggled", "Space Toggled", true)
        var voidCheck = BooleanValue("Void Check", "Void Check", true)
        var behindValue = BooleanValue("Behind", "Behind", false)
        var change = BooleanValue("Direction", "Direction", true)
        var render = BooleanValue("Render", "Render", true)
        var hurt = BooleanValue("Hurt", "Hurt", true)
        var direction = true

        fun canStrafe(): Boolean {
            val press = !targetkey.value || mc.gameSettings.keyBindJump.isKeyDown
            return KillAura.target != null && (!hurt.value || mc.thePlayer.hurtTime > 0) && press
        }

        fun getRotationFromEyeHasPrev(target: EntityLivingBase?): Rotation {
            val x = target!!.prevPosX + (target.posX - target.prevPosX)
            val y = target.prevPosY + (target.posY - target.prevPosY)
            val z = target.prevPosZ + (target.posZ - target.prevPosZ)
            return getRotationFromEyeHasPrev(x, y, z)
        }

        fun getRotationFromEyeHasPrev(x: Double, y: Double, z: Double): Rotation {
            val xDiff = x - (mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX))
            val yDiff = y - (mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) + (mc.thePlayer.entityBoundingBox.maxY - mc.thePlayer.entityBoundingBox.minY))
            val zDiff = z - (mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ))
            val dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff).toDouble()

            return Rotation((atan2(zDiff, xDiff) * 180f / Math.PI).toFloat() - 90f, -(atan2(yDiff, dist) * 180f / Math.PI).toFloat())
        }

        val isAboveVoid: Boolean
            get() {
                if (mc.thePlayer.posY < 0) return true
                for (i in (mc.thePlayer.posY - 1).toInt() downTo 1)
                    if (mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, i.toDouble(), mc.thePlayer.posZ)).block !is BlockAir) return false

                return !mc.thePlayer.onGround
            }
    }
}