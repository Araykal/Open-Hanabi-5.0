package life.hanabi.modules.world

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventDamageBlock
import life.hanabi.event.events.impl.player.EventMotion
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class SpeedMine(name: String) : Module(name, ModuleCategory.World) {
    var blockPos: BlockPos? = null
    var facing: EnumFacing? = null
    var curPacket: C07PacketPlayerDigging? = null
    var pot = BooleanValue("Pot Boost", false)
    var c03 = BooleanValue("Extra PP", false)
    var packet = true
    var damage = true
    private var bzs = false
    private var bzx = 0.0f

    init {
        addValues(speed, Pspeed, pot, c03)
    }

    @EventTarget
    fun onDamageBlock(event: EventPacketReceived) {
        if (event.packet is C07PacketPlayerDigging && event.packet != curPacket && !mc.playerController.extendedReach() && mc.playerController != null) {
            val c07PacketPlayerDigging = event.packet as C07PacketPlayerDigging
            if (c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                bzs = true
                blockPos = c07PacketPlayerDigging.position
                facing = c07PacketPlayerDigging.facing
                bzx = 0.0f
                mc.timer.timerSpeed = 1.0f
            } else if (c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.status == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                bzs = false
                blockPos = null
                facing = null
                mc.timer.timerSpeed = 1.0f
            }
        }
    }

    private fun canBreak(pos: BlockPos): Boolean {
        val blockState = mc.theWorld.getBlockState(pos)
        val block = blockState.block
        return block.blockHardness != -1f
    }

    @EventTarget
    fun onBreak(event: EventDamageBlock) {
        if (canBreak(event.pos)) {
            if (pot.value) {
                mc.thePlayer.addPotionEffect(PotionEffect(Potion.digSpeed.getId(), 100, 1))
            }
        }
    }

    @EventTarget
    fun onUpdate(event: EventMotion) {
        val controller = mc.playerController
        if (mc.playerController.extendedReach()) {
            controller.setBlockHitDelay(0)
            if (controller.curBlockDamageMP >= 1 - speed.value.toFloat()) {
                controller.curBlockDamageMP = 1f
            }
        } else if (bzs) {
            val block = mc.theWorld.getBlockState(blockPos).block
            bzx += block.getPlayerRelativeBlockHardness(mc.thePlayer) * Pspeed.value.toFloat()
            if (bzx >= 1.0f) {
                mc.theWorld.setBlockState(blockPos, Blocks.air.defaultState, 11)
                val packet = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing)
                curPacket = packet
                mc.thePlayer.sendQueue.networkManager.sendPacket(packet)
                bzx = 0.0f
                bzs = false
            }
        }
    }

    companion object {
        var speed = NumberValue("Speed", 0.8f, 0.3f, 1.0f, 0.1f)
        var Pspeed = NumberValue("PacketSpeed", 1.6f, 1.0f, 3.0f, 0.1f)
    }
}