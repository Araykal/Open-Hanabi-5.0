package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ModeValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.BlockBed
import net.minecraft.block.BlockCake
import net.minecraft.block.BlockDragonEgg
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3i
import kotlin.math.atan2

class Nuker(name: String) : Module(name, ModuleCategory.World) {
    private val timer = TimerUtil()
    private val mode = ModeValue("Mode", "Bed", "Bed", "Egg", "Cake")
    private val reach = NumberValue("Reach", "Reach", 6.0, 1.0, 6.0, 0.1)
    private val delay = NumberValue("Delay", "Delay", 120.0, 0.0, 1000.0, 10.0)
    private val instant = BooleanValue("Instant", "Instant", false)

    init {
        addValues(mode, reach, delay, instant)
    }

    @EventTarget
    fun onPre(event: EventMotion) {
        if (event.isPost) return
        val positions: Iterator<BlockPos> = BlockPos.getAllInBox(mc.thePlayer.position.subtract(Vec3i(reach.value.toInt(), reach.value.toInt(), reach.value.toInt())), mc.thePlayer.position.add(Vec3i(reach.value.toInt(), reach.value.toInt(), reach.value.toInt()))).iterator()
        var bedPos: BlockPos? = null

        while (positions.hasNext()) {
            val cur = positions.next()
            if (mc.theWorld.getBlockState(cur).block is BlockBed && mode.current == "Bed" || mc.theWorld.getBlockState(cur).block is BlockDragonEgg && mode.current == "Egg" || mc.theWorld.getBlockState(cur).block is BlockCake && mode.current == "Cake") {
                bedPos = cur
            }
        }

        if (bedPos != null) {
            val rot = getRotationsNeededBlock(bedPos.x.toDouble(), bedPos.y.toDouble(), bedPos.z.toDouble())
            event.yaw = rot[0]
            event.pitch = rot[1]
            if (timer.delay(delay.value.toInt().toFloat())) {
                if (instant.value) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN))
                    mc.thePlayer.swingItem()
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bedPos, EnumFacing.DOWN))
                } else {
                    if (mc.playerController.blockHitDelay > 1) {
                        mc.playerController.setBlockHitDelay(1)
                    }
                    mc.thePlayer.swingItem()
                    val direction = getClosestEnum(bedPos)
                    if (direction != null) {
                        mc.playerController.onPlayerDamageBlock(bedPos, direction)
                    }
                }
                mc.thePlayer.sendQueue.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN))
                mc.thePlayer.sendQueue.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, bedPos, EnumFacing.DOWN))
                mc.thePlayer.sendQueue.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, bedPos, EnumFacing.DOWN))

                mc.thePlayer.swingItem()
                timer.reset()
            }
        }
    }

    private fun getClosestEnum(pos: BlockPos): EnumFacing {
        var closestEnum = EnumFacing.UP
        val rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0])
        if (rotations in 45.0..135.0) {
            closestEnum = EnumFacing.EAST
        } else if (rotations in 135.0..180.0 || rotations <= -135 && rotations >= -180) {
            closestEnum = EnumFacing.SOUTH
        } else if (rotations <= -45 && rotations >= -135) {
            closestEnum = EnumFacing.WEST
        } else if (rotations >= -45 && rotations <= 0 || rotations in 0.0..45.0) {
            closestEnum = EnumFacing.NORTH
        }
        if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75 || MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75) {
            closestEnum = EnumFacing.UP
        }
        return closestEnum
    }

    fun getRotations(block: BlockPos, face: EnumFacing): FloatArray {
        val x = block.x + 0.5 - mc.thePlayer.posX
        val z = block.z + 0.5 - mc.thePlayer.posZ
        val d1 = mc.thePlayer.posY + mc.thePlayer.eyeHeight - (block.y + 0.5)
        val d3 = MathHelper.sqrt_double(x * x + z * z).toDouble()
        var yaw = (atan2(z, x) * 180.0 / Math.PI).toFloat() - 90.0f
        val pitch = (atan2(d1, d3) * 180.0 / Math.PI).toFloat()
        if (yaw < 0.0f) {
            yaw += 360f
        }
        return floatArrayOf(yaw, pitch)
    }

    companion object {
        fun getRotationsNeededBlock(x: Double, y: Double, z: Double): FloatArray {
            val diffX = x + 0.5 - mc.thePlayer.posX
            val diffZ = z + 0.5 - mc.thePlayer.posZ
            val diffY = y + 0.5 - (mc.thePlayer.posY + mc.thePlayer.eyeHeight.toDouble())
            val dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
            val yaw = (atan2(diffZ, diffX) * 180.0 / Math.PI).toFloat() - 90.0f
            val pitch = (-atan2(diffY, dist) * 180.0 / Math.PI).toFloat()

            return floatArrayOf(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch))
        }
    }
}