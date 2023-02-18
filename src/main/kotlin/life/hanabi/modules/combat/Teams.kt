package life.hanabi.modules.combat

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity

class Teams(name: String) : Module(name, ModuleCategory.Combat) {
    companion object {
        fun isOnSameTeam(entity: Entity): Boolean {
            if (!ModuleManager.modules["Teams"]!!.stage) return false
            if (mc.thePlayer.displayName.unformattedText.startsWith("\u00a7")) {
                if (mc.thePlayer.displayName.unformattedText.length <= 2 || entity.displayName.unformattedText.length <= 2) {
                    return false
                }
                return mc.thePlayer.displayName.unformattedText.substring(0, 2) == entity.displayName.unformattedText.substring(0, 2)
            }
            return false
        }
    }
}