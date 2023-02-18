package life.hanabi.modules.pvp

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.misc.EventTick
import org.lwjgl.opengl.Display

class SnapLook(name: String) : Module(name, ModuleCategory.PVP) {

    @EventTarget
    fun onTick(e: EventTick) {
        if (!perspectiveToggled) {
            if (mc.gameSettings.keyBindToggleSnapLook.isKeyDown) {
                perspectiveToggled = true
                cameraYaw = mc.thePlayer.rotationYaw
                cameraPitch = mc.thePlayer.rotationPitch
                previousPerspective = mc.gameSettings.thirdPersonView
                mc.gameSettings.thirdPersonView = 1
            }
        } else if (!mc.gameSettings.keyBindToggleSnapLook.isKeyDown) {
            perspectiveToggled = false
            mc.gameSettings.thirdPersonView = previousPerspective
        }
    }

    companion object {
        var perspectiveToggled = false
        private var cameraYaw = 0f
        private var cameraPitch = 0f
        private var previousPerspective = 0

        fun getCameraYaw(): Float {
            return if (perspectiveToggled) cameraYaw else mc.renderViewEntity.rotationYaw
        }

        fun getCameraPitch(): Float {
            return if (perspectiveToggled) cameraPitch else mc.renderViewEntity.rotationPitch
        }

        fun getCameraPrevYaw(): Float {
            return if (perspectiveToggled) cameraYaw else mc.renderViewEntity.prevRotationYaw
        }

        fun getCameraPrevPitch(): Float {
            return if (perspectiveToggled) cameraPitch else mc.renderViewEntity.prevRotationPitch
        }

        fun overrideMouse(): Boolean {
            if (mc.inGameHasFocus && Display.isActive()) {
                if (!perspectiveToggled) {
                    return true
                }
                mc.mouseHelper.mouseXYChange()
                val f1 = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f
                val f2 = f1 * f1 * f1 * 8.0f
                val f3 = mc.mouseHelper.deltaX * f2
                val f4 = mc.mouseHelper.deltaY * f2
                cameraYaw += f3 * 0.15f
                cameraPitch += f4 * 0.15f
                if (cameraPitch > 90f) {
                    cameraPitch = 90f
                }
                if (cameraPitch < -90f) {
                    cameraPitch = -90f
                }
            }
            return false
        }

        init {
            perspectiveToggled = false
            cameraYaw = 0.0f
            cameraPitch = 0.0f
            previousPerspective = 0
        }
    }
}