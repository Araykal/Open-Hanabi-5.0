package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.NumberValue

class MoreParticles(name: String) : Module(name, ModuleCategory.Render) {
    @JvmField
    var critParticles = NumberValue("CritParticles", "Always show crit particles.", 2, 0, 10, 1)
    @JvmField
    var sharpParticles = NumberValue("SharpnessParticles", "Always show sharpness particles.", 1, 0, 10, 1)
    @JvmField
    var lavaParticles = NumberValue("BloodParticles", "Show lava particles when hitting.", 0, 0, 10, 1)
    @JvmField
    var sound = BooleanValue("HitSound", "Play sound when hitting", false)

    init {
        addValues(critParticles, sharpParticles, lavaParticles, sound)
    }
}