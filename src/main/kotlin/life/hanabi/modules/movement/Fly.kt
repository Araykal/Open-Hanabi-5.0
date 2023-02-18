package life.hanabi.modules.movement

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.ModeValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventMove
import life.hanabi.utils.MovementUtils

class Fly(name: String) : Module(name, ModuleCategory.Movement) {
    private val modeProperty = ModeValue("Flight mode", "Motion", "Motion", "Creative")

    init {
        addValues(modeProperty)
    }

    @EventTarget
    fun onMotion(event: EventMotion) {
        if (MovementUtils.isMoving()) {
            when (modeProperty.current) {
                "Motion" -> mc.thePlayer.motionY = 0.0
                "Creative" -> {
                    mc.thePlayer.capabilities.isFlying = true
                    mc.thePlayer.capabilities.allowFlying = true
                }
            }
        }
    }

    @EventTarget
    fun onMove(e: EventMove) {
        if (MovementUtils.isMoving()) {
            when (modeProperty.current) {
                "Motion" -> {
                    mc.thePlayer.motionY = if (mc.gameSettings.keyBindJump.isKeyDown) 0.5 else if (mc.gameSettings.keyBindSneak.isKeyDown) -0.5 else 0.0
                    mc.thePlayer.motionX = mc.thePlayer.movementInput.moveForward * 0.5
                    mc.thePlayer.motionZ = mc.thePlayer.movementInput.moveStrafe * 0.5
                }
            }
        }
    }

    override fun onDisable() {
        if (modeProperty.current == "Creative") {
            mc.thePlayer.capabilities.isFlying = false
            mc.thePlayer.capabilities.allowFlying = false
        }
    }
}