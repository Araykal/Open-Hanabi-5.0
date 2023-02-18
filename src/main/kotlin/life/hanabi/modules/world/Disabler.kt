package life.hanabi.modules.world

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventLoadWorld
import life.hanabi.event.events.impl.client.EventPacketSend
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.gui.notification.Info
import life.hanabi.gui.notification.Notification
import life.hanabi.utils.math.TimerUtil
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

@IIiIIiiiIiii
class Disabler(name: String) : Module(name, ModuleCategory.World) {
    private val confirmTransactionQueue: Queue<C0FPacketConfirmTransaction> = ConcurrentLinkedQueue()
    private val keepAliveQueue: Queue<C00PacketKeepAlive> = ConcurrentLinkedQueue()
    private val clickWindowPackets = CopyOnWriteArrayList<C0EPacketClickWindow>()
    private var disabled = false
    private var lastuid = 0
    var isCraftingItem = false
    private val lastRelease = TimerUtil()
    private var cancelledPackets = 0
    private val timer2 = TimerUtil()

    override fun onEnable() {
        super.onEnable()
    //    Hanabi.INSTANCE.notificationsManager.add(Info("This is not fully tested, use at your own risk.", Notification.Type.Warning))
    }

    @EventTarget
    private fun loadWorld(e: EventLoadWorld) {
        timer2.reset()
        confirmTransactionQueue.clear()
        keepAliveQueue.clear()
        disabled = false
        isCraftingItem = false
        clickWindowPackets.clear()
        lastuid = 0
        cancelledPackets = 0
    }

    @EventTarget
    private fun onPacket(e: EventPacketSend) {
        val packet = e.getPacket()
        if (disabled) {
            if (packet is C03PacketPlayer && !(packet is C04PacketPlayerPosition || packet is C05PacketPlayerLook || packet is C06PacketPlayerPosLook) && !ModuleManager.modules["Scaffold"]!!.stage) {
                cancelledPackets++
                e.isCancelled = true
            }
        }
        if (ModuleManager.modules["Speed"]!!.stage) {
            if (e.getPacket() is C0CPacketInput) {
                val p = e.getPacket() as C0CPacketInput
                p.strafeSpeed = p.strafeSpeed * 0.9999f
                e.packet = p
            }
        }
        if (packet is C0FPacketConfirmTransaction) {
            if (packet.windowId == 0 && packet.uid < 0 && packet.uid.toInt() != -1) {
                if (disabled) {
                    cancelledPackets++
                    e.isCancelled = true
                }
            }
        }
        if (packet is C0FPacketConfirmTransaction) {
            processConfirmTransactionPacket(e)
        } else if (packet is C00PacketKeepAlive) {
            processKeepAlivePacket(e)
        }
    }

    @EventTarget
    private fun onUpdate(e: EventMotion) {
        if (e.getType() == EventType.PRE) {
            if (mc.thePlayer.ticksExisted % 40 == 0) {
                cancelledPackets = 0
            }
        }
        setSuffix(if (disabled) "Active" else "Progressing")
        if (disabled) {
            if (confirmTransactionQueue.isEmpty()) {
                lastRelease.reset()
            } else {
                if (confirmTransactionQueue.size >= 7) {
                    while (!keepAliveQueue.isEmpty()) mc.netHandler.addToSendQueueNoEvent(keepAliveQueue.poll())
                    while (!confirmTransactionQueue.isEmpty()) {
                        mc.netHandler.addToSendQueueNoEvent(confirmTransactionQueue.poll())
                    }
                }
            }
        }
    }

    private fun processConfirmTransactionPacket(e: EventPacketSend) {
        val packet = e.getPacket()
        val preuid = lastuid - 1
        if (packet is C0FPacketConfirmTransaction) {
            if (packet.windowId == 0 || packet.uid < 0) {
                if (packet.uid.toInt() == preuid) {
                    if (!disabled) {
                        Hanabi.INSTANCE.notificationsManager.add(Info("Disabled WatchDog", Notification.Type.Success))
                        disabled = true
                    }
                    confirmTransactionQueue.offer(packet)
                    e.isCancelled = true
                }
                lastuid = packet.uid.toInt()
            }
        }
    }

    private fun processKeepAlivePacket(e: EventPacketSend) {
        val packet = e.getPacket()
        if (packet is C00PacketKeepAlive) {
            if (disabled) {
                keepAliveQueue.offer(packet)
                e.isCancelled = true
            }
        }
    }
}