package life.hanabi.modules.movement

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventMotion
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockPos

class Eagle(name: String) : Module(name, ModuleCategory.PVP) {
    fun getBlockUnderPlayer(player: EntityPlayer): Block {
        return mc.theWorld.getBlockState(BlockPos(player.posX, player.posY - 1.0, player.posZ)).block
    }

    @EventTarget
    fun onUpdate(event: EventMotion) {
        if (!mc.thePlayer.onGround) return
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.keyCode, getBlockUnderPlayer(mc.thePlayer) is BlockAir)
    }

    override fun onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) KeyBinding.setKeyBindState(
            mc.gameSettings.keyBindSneak.keyCode, false
        )
    }
}