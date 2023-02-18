package life.hanabi.modules.render

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.EventRender2D

class NotificationModule(name: String) : Module(name, ModuleCategory.Render) {
    init {
        addValues(showModuleToggle)
    }

    @EventTarget
    fun onRender2D(e: EventRender2D) {
        Hanabi.INSTANCE.notificationsManager.draw()
    }

    companion object {
        @JvmField
        var showModuleToggle = BooleanValue("ShowModule Toggle", "Show message when some modules toggled", true)
    }
}