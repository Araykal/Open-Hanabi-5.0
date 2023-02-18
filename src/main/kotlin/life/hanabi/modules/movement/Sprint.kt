package life.hanabi.modules.movement

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.gui.customgui.GuiInput
import org.lwjgl.input.Keyboard

class Sprint(name: String) : Module(name, ModuleCategory.Movement) {
    var always = BooleanValue("Always", false)

    init {
        addValues(always)
        key = Keyboard.KEY_I
    }

    override fun onRight() {
        super.onRight()
        mc.displayGuiScreen(GuiInput())
    }

    @EventTarget
    fun onMotion(e: EventMotion) {
        if (e.type == EventType.PRE) {
            if (always.value) mc.thePlayer.isSprinting = !mc.thePlayer.isDead && mc.thePlayer.foodStats.foodLevel > 6 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking && !mc.thePlayer.isBlocking && !mc.thePlayer.isUsingItem else mc.thePlayer.isSprinting = !mc.thePlayer.isDead && mc.thePlayer.foodStats.foodLevel > 6 && mc.thePlayer.moveForward > 0.0f && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking && !mc.thePlayer.isBlocking && !mc.thePlayer.isUsingItem
        }
    }
}