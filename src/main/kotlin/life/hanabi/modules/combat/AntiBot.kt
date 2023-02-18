package life.hanabi.modules.combat

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ModeValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import java.util.concurrent.CopyOnWriteArrayList

class AntiBot(name: String) : Module(name, ModuleCategory.Combat) {
    var remove = BooleanValue("AntiBot", "Removed", true)
    var count = 0

    init {
        addValues(mode, remove)
    }

    @EventTarget
    fun onUpdate(e: EventMotion) {
        displayName = mode.current
        if (mc.isSingleplayer) return
        if (mode.value == "MineLand") {
            if (mc.theWorld.getLoadedEntityList().isNotEmpty()) {
                for (ent in mc.theWorld.getLoadedEntityList()) {
                    if (ent is EntityPlayer) {
                        if (!invalid.contains(ent) && mc.thePlayer.getDistanceToEntity(ent) > 20) {
                            invalid.add(ent)
                        }
                        if (ent != mc.thePlayer && !invalid.contains(ent) && mc.thePlayer.getDistanceToEntity(ent) < 10) {
                            mc.theWorld.removeEntity(ent)
                        }
                    }
                }
            }
        }
        if (mc.thePlayer.ticksExisted < 5) whitelist.clear()
        if (mc.thePlayer.ticksExisted % 60 == 0) whitelist.clear()
        if (mode.value == "Hypixel") {
            if (mc.theWorld.getLoadedEntityList().isNotEmpty()) {
                for (ent in mc.theWorld.getLoadedEntityList()) {
                    if (ent is EntityPlayer) {
                        if (!whitelist.contains(ent)) {
                            val formatted = ent.getDisplayName().formattedText
                            if (formatted.startsWith("\u00a7r\u00a78[NPC]")) return
                            if (!ent.isInvisible()) whitelist.add(ent)
                            if (ent.hurtResistantTime == 8) whitelist.add(ent)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val mode = ModeValue("Mode", "Hypixel", "Hypixel", "Mineplex", "Advanced", "MineLand", "HuaYuTing")
        private val invalid: MutableList<Entity> = CopyOnWriteArrayList()
        private val whitelist: MutableList<Entity> = CopyOnWriteArrayList()

        private fun inTab(entity: EntityLivingBase): Boolean {
            for (info in Minecraft.getMinecraft().netHandler.playerInfoMap) if (info != null && info.gameProfile != null && info.gameProfile.name.contains(
                    entity.name
                )
            ) return true
            return false
        }

        fun isBot(e: Entity): Boolean {
            if (e !is EntityPlayer || !ModuleManager.modules["AntiBot"]!!.stage) return false
            return if (mode.value == "Hypixel") {
                !inTab(e) && !whitelist.contains(e)
            } else mode.value == "Mineplex" && !java.lang.Float.isNaN(e.health)
        }
    }
}