package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue

class OldAnimation(name: String) : Module(name, ModuleCategory.Render) {
    init {
        addValues(oldBlock, oldRod, blockHit, oldBow, oldSwing)
        canBeEnabled = false
    }

    companion object {
        @JvmField
        var oldRod = BooleanValue("OldRod", "Old Rod Animation", false)
        var oldBlock = BooleanValue("OldBlock", "Old Block Animation", false)
        @JvmField
        var blockHit = BooleanValue("BlockHit", "Block when hitting", false)
        @JvmField
        var oldBow = BooleanValue("OldBow", "Old Bow Animation", false)
        @JvmField
        var oldSwing = BooleanValue("OldSwing", "oldSwing", false)
    }
}