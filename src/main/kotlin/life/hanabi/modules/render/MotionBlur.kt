package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.DisplayFrameEvent
import org.lwjgl.opengl.GL11

class MotionBlur(name: String) : Module(name, ModuleCategory.Render) {
    var multiplier = NumberValue("FrameMultiplier", "FrameMultiplier", 0.5, 0.05, 0.99, 0.05)

    init {
        addValues(multiplier)
    }

    @EventTarget
    fun onClientTick(event: DisplayFrameEvent) {
        val n = multiplier.value
        GL11.glAccum(259, n.toFloat())
        GL11.glAccum(256, (1.0 - n).toFloat())
        GL11.glAccum(258, 1.0f)
    }
}