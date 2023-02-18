package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.utils.drawTracer
import life.hanabi.utils.square
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import java.awt.Color
import kotlin.math.roundToInt

class Tracers(name: String) : Module(name, ModuleCategory.Render) {

    @EventTarget
    private fun on3DRender(e: EventRender3D) {
        for (o in mc.theWorld.loadedEntityList) {
            val entity = o as Entity
            if (!entity.isEntityAlive || entity !is EntityPlayer || entity == mc.thePlayer) continue

            val color = Color(255,50,50).rgb
            drawTracer(Vec3(entity.posX, entity.posY, entity.posZ),color)
        }
    }

}