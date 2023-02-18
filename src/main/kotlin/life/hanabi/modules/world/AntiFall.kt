package life.hanabi.modules.world

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.client.EventPacketSend
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.BlockAir
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos

class AntiFall(name: String) : Module(name, ModuleCategory.World) {
    var onlyvoid = BooleanValue("OnlyVoid", "OnlyVoid", true)
    var nodmg = BooleanValue("0 DMG", "0 DMG", true)
    var last = doubleArrayOf(0.0, 0.0, 0.0)
    var timerUtil = TimerUtil()
    var packets = ArrayList<Packet<*>>()

    init {
        addValues(falldistance, delay, onlyvoid, nodmg)
    }

    @EventTarget
    fun onSend(e: EventPacketSend) {
        //clear
        if (mc.thePlayer != null && mc.thePlayer.ticksExisted < 100) {
            packets.clear()
            return
        }
        if (e.packet is C03PacketPlayer) {
            if (isAboveVoid) {
                e.isCancelled = true
                packets.add(e.packet)
                if (timerUtil.hasReached(delay.value.toInt().toDouble())) {
                    mc.thePlayer.setPosition(last[0], last[1], last[2])
                    mc.thePlayer.motionY = 0.0
                    packets.clear()
                    timerUtil.reset()
                }
            } else {
                last = doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)

                if (packets.size > 0) {
                    for (packet in packets) {
                        mc.netHandler.addToSendQueueNoEvent(packet)
                    }
                    packets.clear()
                }
                timerUtil.reset()
            }
        }
    }

    @EventTarget
    fun onReceive(e: EventPacketReceived) {
        if (e.packet is S08PacketPlayerPosLook) {
            packets.clear()
        }
    }

    companion object {
        var falldistance = NumberValue("FallDistance", "FallDistance", 10.0, 0.0, 30.0, 0.1)
        var delay = NumberValue("Delay", "Delay", 800.0, 200.0, 3000.0, 100.0)
        val isAboveVoid: Boolean
            get() {
                if (mc.thePlayer == null) {
                    return false
                }
                if (mc.thePlayer.posY < 0) return true
                for (i in (mc.thePlayer.posY - 1).toInt() downTo 1) if (mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX, i.toDouble(), mc.thePlayer.posZ)).block !is BlockAir) return false

                return !mc.thePlayer.onGround
            }
    }
}