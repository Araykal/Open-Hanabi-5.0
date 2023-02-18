package life.hanabi.modules.movement

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.ModeValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventNoSlow
import life.hanabi.utils.MathUtils
import life.hanabi.utils.MovementUtils
import life.hanabi.utils.NetworkUtils
import life.hanabi.utils.PlayerUtil
import net.minecraft.network.play.client.C09PacketHeldItemChange

class NoSlow(name: String) : Module(name, ModuleCategory.Movement) {
    private val modeProperty = ModeValue("No Slow", "Watchdog", "Watchdog", "NCP", "Vanilla")
    var slot = 0
    var originSlot = 0
    var changed = false

    init {
        addValues(modeProperty)
    }

    @EventTarget
    fun onUpdate(event: EventMotion) {
        when (modeProperty.current) {
            "Vanilla" -> {}
            "NCP", "Watchdog" -> if (mc.thePlayer.heldItem != null && mc.gameSettings.keyBindUseItem.isKeyDown && MovementUtils.isMoving()) {
                mc.thePlayer.isSprinting = true
                mc.gameSettings.keyBindSprint.pressed = true
                originSlot = PlayerUtil.getSlotByItem(mc.thePlayer.heldItem.item)

                if (!changed) {
                    slot = if (PlayerUtil.getSlotByItem(mc.thePlayer.heldItem.item) == 8) 0 else MathUtils.getRandomInRange(1, 7)
                    changed = true
                    NetworkUtils.sendPacketNoEvent(C09PacketHeldItemChange(slot))
                    NetworkUtils.sendPacketNoEvent(C09PacketHeldItemChange(originSlot))
                }
            } else changed = false
        }
    }

    @EventTarget
    fun onNoSlow(event: EventNoSlow) {
        if (mc.thePlayer.isUsingItem && MovementUtils.isMoving()) event.isCancelled = true
    }
}