package life.hanabi.modules.pvp

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.misc.EventTick
import life.hanabi.utils.math.TimerUtil
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.gui.ScaledResolution
import java.awt.Color

class MemoryManager(name: String) : Module(name, ModuleCategory.PVP) {
    var autoGc = BooleanValue("AutoGC", "Auto free memory", true)
    var gcLimit = NumberValue("GCLimit", "Limit of gc.", 60, 0, 100, 1)
    var timer = TimerUtil()
    var maxmem: Long = 0
    var totalmem: Long = 0
    var freemem: Long = 0
    var usemem: Long = 0
    var pct = 0f

    init {
        addValues(autoGc, gcLimit, fastLoad, display)
    }

    override fun onGui() {
        super.onGui()
        if (!display.value) {
            return
        }
        val sr = ScaledResolution(mc)
        val rX = x * sr.scaledWidth
        val rY = y * sr.scaledHeight
        width = 150f
        height = 15f
        RenderUtil.drawRect(rX, rY, rX + 150, rY + 3, Color(0, 0, 0, 120).rgb)
        RenderUtil.drawRect(rX.toDouble(), rY.toDouble(), (rX + 150 * pct / 100).toDouble(), (rY + 3).toDouble(), Hanabi.INSTANCE.theme.themeColor)

        mc.fontRendererObj.drawStringWithShadow("Max:" + maxmem / 1024 / 1024 + "m Used:" + usemem / 1024 / 1024 + "m  PCT:" + pct.toInt(), rX, rY + 6, Color(240, 240, 240).rgb)
    }

    @EventTarget
    fun onTick(e: EventTick) {
        if (autoGc.value) {
            maxmem = Runtime.getRuntime().maxMemory()
            totalmem = Runtime.getRuntime().totalMemory()
            freemem = Runtime.getRuntime().freeMemory()
            usemem = totalmem - freemem
            pct = usemem * 100f / maxmem
            if (timer.delay(1000f) && gcLimit.value.toFloat() <= pct) {
                Runtime.getRuntime().gc()
                timer.reset()
            }
        }
    }

    companion object {
        @JvmField
        var fastLoad = BooleanValue("FastLoad", "Cancel some gc", true)
        var display = BooleanValue("display", "Display the memory", false)
    }
}