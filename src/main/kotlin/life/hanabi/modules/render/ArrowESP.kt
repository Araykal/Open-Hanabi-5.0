package life.hanabi.modules.render

import com.google.common.collect.Maps
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.EventRender2D
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.modules.combat.AntiBot.Companion.isBot
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import org.lwjgl.opengl.Display
import java.awt.Color
import java.util.function.Consumer

class ArrowESP(name: String) : Module(name, ModuleCategory.Render) {
    private var alpha = 0
    private val size = NumberValue("Size", "Size", 10f, 5f, 25f, 0.1f)
    private val radius = NumberValue("Radius", "Radius", 45f, 10f, 200f, 1f)
    private val entityListener = EntityListener()
    private val players = BooleanValue("Players", "Players", true)
    private val animals = BooleanValue("Animals", "Animals", true)
    private val mobs = BooleanValue("Mobs", "Mobs", false)
    private val invisibles = BooleanValue("Invisibles", "Invisibles", false)
    private val passives = BooleanValue("Passives", "Passives", true)
    private val color = ColorValue("Color", Color(255, 255, 255, 100).rgb)

    init {
        addValues(size, radius, players, animals, mobs, invisibles, passives, color)
    }

    override fun onEnable() {
        alpha = 0
    }

    @EventTarget
    fun onRender3D(event: EventRender3D) {
        entityListener.render3d()
    }

    @EventTarget
    fun onRender2D(event: EventRender2D) {
        mc.theWorld.loadedEntityList.forEach(Consumer { o: Entity ->
            if (o is EntityLivingBase && isValid(o)) {
                val pos = entityListener.getEntityLowerBounds()[o]
                if (pos != null) {
                    val x = Display.getWidth() / 2f / if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale
                    val y = Display.getHeight() / 2f / if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale
                    val yaw = getRotations(o) - mc.thePlayer.rotationYaw
                    GlStateManager.pushMatrix()
                    RenderUtil.startSmooth()
                    GlStateManager.translate(x, y, 0f)
                    GlStateManager.rotate(yaw, 0f, 0f, 1f)
                    GlStateManager.translate(-x, -y, 0f)
                    RenderUtil.drawTracerPointer(x, y - radius.value.toFloat(), size.value.toFloat(), 2f, 1f, getColor(o, alpha).rgb)
                    GlStateManager.translate(x, y, 0f)
                    GlStateManager.rotate(-yaw, 0f, 0f, 1f)
                    GlStateManager.translate(-x, -y, 0f)
                    RenderUtil.endSmooth()
                    GlStateManager.popMatrix()
                }
            }
        })
    }

    private fun isOnScreen(pos: Vec3): Boolean {
        return if (pos.xCoord > -1 && pos.zCoord < 1) pos.xCoord / (if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale) >= 0 && pos.xCoord / (if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale) <= Display.getWidth() && pos.yCoord / (if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale) >= 0 && pos.yCoord / (if (mc.gameSettings.guiScale == 0) 1 else mc.gameSettings.guiScale) <= Display.getHeight() else false
    }

    private fun isValid(entity: EntityLivingBase): Boolean {
        return !isBot(entity) && entity != mc.thePlayer && isValidType(entity) && entity.entityId != -1488 && entity.isEntityAlive && (!entity.isInvisible || invisibles.value)
    }

    private fun isValidType(entity: EntityLivingBase): Boolean {
        return players.value && entity is EntityPlayer || mobs.value && (entity is EntityMob || entity is EntitySlime) || passives.value && (entity is EntityVillager || entity is EntityGolem) || animals.value && entity is EntityAnimal
    }

    private fun getRotations(ent: EntityLivingBase): Float {
        val x = ent.posX - mc.thePlayer.posX
        val z = ent.posZ - mc.thePlayer.posZ
        return (-(Math.atan2(x, z) * 57.29577951308232)).toFloat()
    }

    private fun getColor(player: EntityLivingBase, alpha: Int): Color {
        val f = mc.thePlayer.getDistanceToEntity(player)
        val f1 = 50f
        val f2 = Math.max(0.0f, Math.min(f, f1) / f1)
        val design = color.color
        val red = (design shr 16 and 255).toFloat() / 255.0f
        val green = (design shr 8 and 255).toFloat() / 255.0f
        val blue = (design and 255).toFloat() / 255.0f
        val clr = Color(red, green, blue, f2)
        return Color(clr.red, clr.green, clr.blue, ((255 - clr.alpha) / 1.3f).toInt())
    }

    class EntityListener {
        private val entityUpperBounds: MutableMap<Entity, Vec3> = Maps.newHashMap()
        private val entityLowerBounds: MutableMap<Entity, Vec3> = Maps.newHashMap()
        public fun render3d() {
            if (entityUpperBounds.isNotEmpty()) {
                entityUpperBounds.clear()
            }
            if (entityLowerBounds.isNotEmpty()) {
                entityLowerBounds.clear()
            }
            for (e in Minecraft.getMinecraft().theWorld.loadedEntityList) {
                val bound = getEntityRenderPosition(e)
                bound.add(Vec3(0.0, e.height + 0.2, 0.0))
                val upperBounds = RenderUtil.to2D(bound.xCoord, bound.yCoord, bound.zCoord)
                val lowerBounds = RenderUtil.to2D(bound.xCoord, bound.yCoord - 2, bound.zCoord)
                if (upperBounds != null && lowerBounds != null) {
                    entityUpperBounds[e] = upperBounds
                    entityLowerBounds[e] = lowerBounds
                }
            }
        }

        private fun getEntityRenderPosition(entity: Entity): Vec3 {
            val partial = mc.timer.renderPartialTicks.toDouble()
            val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - mc.renderManager.viewerPosX
            val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - mc.renderManager.viewerPosY
            val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - mc.renderManager.viewerPosZ
            return Vec3(x, y, z)
        }

        fun getEntityLowerBounds(): Map<Entity, Vec3> {
            return entityLowerBounds
        }
    }
}