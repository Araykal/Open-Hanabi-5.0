package life.hanabi.modules.render

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.utils.drawTracer
import life.hanabi.utils.math.TimerUtil
import net.minecraft.block.BlockOre
import net.minecraft.client.Minecraft
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.S22PacketMultiBlockChange
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import java.awt.Color

var ores = mutableListOf<BlockPos>()

@IIiIIiiiIiii
class Xray(name: String) : Module(name, ModuleCategory.Render) {
    var tracers = BooleanValue("Tracers", false)
    var esp = BooleanValue("ESP", false)
    var lastPos: Vec3 = Vec3(0.0, 0.0, 0.0);

    init {
        addValues(tracers, esp)
    }

    override fun onEnable() {
        mc.renderGlobal.loadRenderers()
    }

    override fun onDisable() {
        mc.renderGlobal.loadRenderers()
        ores.clear()
    }

    @EventTarget
    fun onPacket(e: EventPacketReceived) {
        val packet = e.packet
        if (packet is S23PacketBlockChange) {
            val block = packet.blockState.block
            if (block is BlockOre) {
                if (packet.blockPosition.distanceSq(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ) < 30) {
                    ores.add(packet.blockPosition)
                }
            }
        }
        if (packet is S22PacketMultiBlockChange) {
            val block = packet.changedBlocks
            for (blockUpdateData in block) {
                val block1 = blockUpdateData.blockState
                if (block1.block is BlockOre) {
                    if (blockUpdateData.pos.distanceSq(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ) < 30) {
                        ores.add(blockUpdateData.pos)
                    }
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(e: EventRender3D) {
        val remove = mutableListOf<BlockPos>()
        ores.forEach { ore ->
            if (mc.theWorld.getBlockState(ore).block is BlockOre) {
                var color = Color(0, 0, 0)
                when {
                    mc.theWorld.getBlockState(ore).block.equals(Blocks.coal_ore) -> {
                        color = Color(80, 80, 80)
                    }

                    mc.theWorld.getBlockState(ore).block.equals(Blocks.diamond_ore) -> {
                        color = Color(20, 100, 255)
                    }

                    mc.theWorld.getBlockState(ore).block.equals(Blocks.emerald_ore) -> {
                        color = Color(50, 255, 50)
                    }

                    mc.theWorld.getBlockState(ore).block.equals(Blocks.gold_ore) -> {
                        color = Color(255, 190, 0)
                    }

                    mc.theWorld.getBlockState(ore).block.equals(Blocks.iron_ore) -> {
                        color = Color(255, 150, 255)
                    }
                }
                drawTracer(Vec3(ore).addVector(0.5, 0.5, 0.5), color.rgb)
            } else {
                remove.add(ore)
            }
        }

        if (remove.size > 0) {
            ores.removeAll(remove)
            remove.clear()
        }
    }

    val packetDelay = TimerUtil()

    @EventTarget
    fun onUpdate(e: EventMotion) {
        if (mc.thePlayer.getDistanceSq(lastPos.xCoord, lastPos.yCoord, lastPos.zCoord) > 3) {
            if (packetDelay.delay(50F)) {
                for (i in -10..10) {
                    if (i % 3 == 0) {
//                        mc.netHandler.addToSendQueue()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun contains(block: BlockPos): Boolean {
            return ores.contains(block)
        }
    }
}