package life.hanabi.modules.misc

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketSend
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.utils.math.TimerUtil
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import org.lwjgl.input.Keyboard
import java.util.*

@IIiIIiiiIiii
class InvMove(name: String) : Module(name, ModuleCategory.Misc) {
    var packets = LinkedList<Packet<*>>()


    override fun onEnable() {
        super.onEnable()
    }

    override fun onDisable() {
        packets.clear()
        super.onDisable()
    }

    val timer: TimerUtil = TimerUtil()
    var packetThread = Thread(Runnable {
        var done = false;
        while (!done) {
            if (timer.delay(20F)) {
                if (packets.size > 0) {
                    mc.netHandler.addToSendQueue(packets.poll())
                } else {
                    done = true
                }
                timer.reset()
            }
        }
    })

    @EventTarget
    fun onPacket(e: EventPacketSend) {
        if (mc.currentScreen is GuiContainer) {
            if (e.packet is C03PacketPlayer) {
                if (!((e.packet as C03PacketPlayer).positionX == 0.0 && (e.packet as C03PacketPlayer).positionY == 0.0 && (e.packet as C03PacketPlayer).positionZ == 0.0)) {
                    packets.add(e.packet)
                    e.isCancelled = true
                }
            }
        } else {
            if (e.packet is C03PacketPlayer) {
                if (packets.size > 0) {
                    if (!((e.packet as C03PacketPlayer).positionX == 0.0 && (e.packet as C03PacketPlayer).positionY == 0.0 && (e.packet as C03PacketPlayer).positionZ == 0.0)) {
                        packets.add(e.packet)
                        e.isCancelled = true
                    }
                    synchronized(packetThread) {
                        if (!packetThread.isAlive) {
//                        println("hi")
                            packetThread = Thread {
                                var done = false;
                                while (!done) {
                                    if (timer.delay(20F)) {
                                        if (packets.size > 0) {
                                            mc.netHandler.addToSendQueueNoEvent(packets.poll())
                                        } else {
                                            done = true
                                        }
                                        timer.reset()
                                    }
                                }
                            }
                            packetThread.start()
                        }
                    }
                }
            }
        }
//        println(packets.size)
    }


    @EventTarget
    private fun setKeyStat(e: EventMotion) {
        if (e.isPre) {
            if (mc.currentScreen is GuiContainer) {
                val key = arrayOf(
                    mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack,
                    mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight,
                    mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump
                )
                var array: Array<KeyBinding>
                val length = key.also { array = it }.size
                var i = 0
                while (i < length) {
                    val b = array[i]
                    KeyBinding.setKeyBindState(b.keyCode, Keyboard.isKeyDown(b.keyCode))
                    ++i
                }
            }
        }
    }
}
