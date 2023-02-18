package life.hanabi.modules.misc

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.misc.EventChat
import life.hanabi.gui.notification.Info
import life.hanabi.gui.notification.Notification
import net.minecraft.event.ClickEvent
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ScreenShotHelper

class AutoGG(name: String) : Module(name, ModuleCategory.Misc) {
    var autoScreenShot = BooleanValue("AutoScreenShot", "AutoScreenShot", true)
    var ad = BooleanValue("AD", "Advertisement", true)
    var ap = BooleanValue("AutoPlay", "AutoPlay", true)

    init {
        addValues(autoScreenShot, ad, ap)
    }

    fun sendGG() {
        if (stage) {
            if (ad.value) {
                mc.thePlayer.sendChatMessage("gg  Get good get hanabi client.")
            } else {
                mc.thePlayer.sendChatMessage("gg")
            }
            if (autoScreenShot.value) {
                mc.ingameGUI.chatGUI.printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.framebufferMc))
            }
        }
    }

    @EventTarget
    fun onPacket(event: EventChat) {
        for (cc in event.chatMessage.siblings) {
            val ce = cc.chatStyle.chatClickEvent
            if (ce != null) {
                if (ce.action == ClickEvent.Action.RUN_COMMAND && ce.value.contains("/play")) {
                    Hanabi.INSTANCE.notificationsManager.add(Info("Play again in 3 s", Notification.Type.Success))
                    Thread {
                        try {
                            Thread.sleep(3000L)
                        } catch (a: InterruptedException) {
                            a.printStackTrace()
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(C01PacketChatMessage(ce.value))
                    }.start()
                    event.isCancelled = true
                }
            }
        }
    }
}