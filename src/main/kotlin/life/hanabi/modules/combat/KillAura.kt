package life.hanabi.modules.combat

import cn.qiriyou.IIiIIiiiIiii
import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.core.values.values.ModeValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.client.EventPacketReceived
import life.hanabi.event.events.impl.player.EventMotion
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.event.events.impl.render.EventRender2D
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.modules.combat.AntiBot.Companion.isBot
import life.hanabi.utils.Colors
import life.hanabi.utils.MathUtils
import life.hanabi.utils.MovementUtils
import life.hanabi.utils.math.AnimationUtils
import life.hanabi.utils.math.ColorUtils
import life.hanabi.utils.math.TimerUtil
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@IIiIIiiiIiii
class KillAura(name: String) : Module(name, ModuleCategory.Combat) {
    private val proiorityValue = ModeValue("Priority", "Health", "Angle", "Distance", "Armor", "Health")

    // TODO: Add Multi / Switch / Single modes
    var minCPSValue = NumberValue("MinCPS", 9, 0, 20, 1)
    var maxCPSValue = NumberValue("MaxCPS", 11, 0, 20, 1)
    var rangeValue = NumberValue("Range", 4.2, 1.0, 6.0, 0.1)
    var blockRangeValue = NumberValue("BlockRange", 3f, 1f, 6f, 0.1f)
    var targetsValue = NumberValue("Max Targets", 3.0, 1.0, 5.0, 1.0)
    var keepSprintValue = BooleanValue("Keep Sprint", true)
    var hoverYaw = NumberValue("HoverYaw", 50f, 0f, 360f, 1f)
    var hoverPitch = NumberValue("HoverPitch", 100f, 0f, 360f, 1f)
    var attacked = ArrayList<EntityLivingBase>()
    var isBlocking = false
    var desiredCps = 10
    var attackTimer = TimerUtil()
    private var healthBarWidth = 0.0
    private var healthBarWidth2 = 0.0
    private var hudHeight = 0.0
    private val targetColor = ColorValue("TargetESP", -1)
    private val esp = BooleanValue("ESP", true)
    private val targetHUD = BooleanValue("TargetHUD", true)
    private val disadventage = BooleanValue("DisAdventage", true)

    init {
        addValues(autoBlockValue, proiorityValue, minCPSValue, maxCPSValue, rangeValue, targetsValue, keepSprintValue, playerValue, mobValue, animalValue, teamValue, onlyOnAim, esp, targetHUD, targetColor, hoverPitch, hoverYaw, blockRangeValue, invisible, disadventage)
    }

    override fun onEnable() {
        target = null
        attackTimer.reset()
        super.onEnable()
    }

    override fun onDisable() {
        target = null
        isBlocking = false
        attackTimer.reset()
        mc.gameSettings.keyBindUseItem.pressed = false
        if (mc.thePlayer != null) {
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
        super.onDisable()
    }

    private fun doBlock(setItemUseInCount: Boolean) {
        if (target!!.health + 1 < mc.thePlayer.health && disadventage.value) return
        if (MathUtils.getRandomInRange(0, 100) > 50) {
            val neededRotations1 = getNeededRotations(target!!, mc.thePlayer)
            if (target!!.getDistanceToEntity(mc.thePlayer) < blockRangeValue.value) {
                if (abs(neededRotations1[0] - target!!.rotationYaw % 360) < hoverYaw.value && abs(neededRotations1[1] - target!!.rotationPitch % 360f) < hoverPitch.value || !onlyOnAim.value || mc.thePlayer.hurtTime > 0f) {
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(if (mc.thePlayer.inventory.currentItem == 8) 7 else mc.thePlayer.inventory.currentItem + 1))
                    mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                    mc.netHandler.addToSendQueueNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))

                    if (setItemUseInCount) {
                        mc.thePlayer.setItemInUse(mc.thePlayer.currentEquippedItem, mc.thePlayer.currentEquippedItem.maxItemUseDuration)
                    }

                    if (target!!.getDistanceToEntity(mc.thePlayer) < blockRangeValue.value.toFloat()) {
                        mc.gameSettings.keyBindUseItem.pressed = true
                        isBlocking = true
                    }
                }
            }
        }
    }

    private fun unBlock(setItemUseInCount: Boolean) {
        if (!isBlocking && disadventage.value) return
        if (setItemUseInCount) {
            mc.thePlayer.setItemInUse(mc.thePlayer.currentEquippedItem, 0)
        }
        mc.gameSettings.keyBindUseItem.pressed = false
        if (target == null) {
            mc.thePlayer.sendQueue.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
        isBlocking = false
    }

    var anim = AnimationUtils()

    @EventTarget
    fun onRender2D(e: EventRender2D) {
        if (mc.thePlayer != null) {
            if (!Hanabi.INSTANCE.loggedIn || Hanabi.INSTANCE.client.flag == 0) {
                mc.thePlayer = null
                mc.thePlayer.jump()
            }
        }
        if (!targetHUD.value) return
        //Distance TH code by Mymylesaws
        val blackcolor = Color(0, 0, 0, 180).rgb
        val blackcolor2 = Color(200, 200, 200, 160).rgb
        val sr2 = ScaledResolution(mc)
        val scaledWidth = sr2.scaledWidth.toFloat()
        val scaledHeight = sr2.scaledHeight.toFloat()
        val font1 = Hanabi.INSTANCE.fontLoaders.arial16
        val nulltarget = target == null
        val x = scaledWidth / 2.0f - 50
        val y = scaledHeight / 2.0f + 32
        val health: Float
        var hpPercentage: Double
        val hurt: Color
        val healthColor: Int
        val healthStr: String
        if (nulltarget) {
            health = 0f
            hpPercentage = (health / 20).toDouble()
            hurt = Color.getHSBColor(300f / 360f, 0f / 10f * 0.37f, 1f)
            healthStr = (0f / 2.0f).toString()
            healthColor = ColorUtils.getHealthColor(0f, 20f).rgb
        } else {
            health = target!!.health
            hpPercentage = (health / target!!.maxHealth).toDouble()
            hurt = Color.getHSBColor(310f / 360f, target!!.hurtTime / 10f, 1f)
            healthStr = (target!!.health.toInt().toFloat() / 2.0f).toString()
            healthColor = ColorUtils.getHealthColor(target!!.health, target!!.maxHealth).rgb
        }
        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0)
        val hpWidth = 140.0 * hpPercentage
        if (nulltarget) {
            healthBarWidth2 = getAnimationStateSmooth(0.0, healthBarWidth2, (6.0 / Minecraft.getDebugFPS()))
            healthBarWidth = getAnimationStateSmooth(0.0, healthBarWidth, (14.0 / Minecraft.getDebugFPS()))
            hudHeight = getAnimationStateSmooth(0.0, hudHeight, (8f / Minecraft.getDebugFPS()).toDouble())
        } else {
            healthBarWidth2 = anim.animate(healthBarWidth2, hpWidth, 0.2, false)
            healthBarWidth = getAnimationStateSmooth(hpWidth, healthBarWidth, (14.0 / Minecraft.getDebugFPS()))
            hudHeight = getAnimationStateSmooth(40.0, hudHeight, (8f / Minecraft.getDebugFPS()).toDouble())
        }
        if (hudHeight == 0.0) {
            healthBarWidth2 = 140.0
            healthBarWidth = 140.0
        }
        GL11.glEnable(3089)
        RenderUtil.doGlScissor(x, y + 40f - hudHeight.toFloat(), x + 140f, y + 40f)
        RenderUtil.drawRect(x, y, x + 140f, y + 40f, blackcolor)
        RenderUtil.drawRect(x, y + 37f, x + 140, y + 40f, Color(0, 0, 0, 49).rgb)
        RenderUtil.drawRect(x, y + 37f, (x + healthBarWidth2).toFloat(), y + 40f, Color(255, 0, 213, 220).rgb)
        RenderUtil.drawGradientSideways(x.toDouble(), y + 37.0, x + healthBarWidth, y + 40.0, Color(0, 81, 179).rgb, healthColor)
        font1.drawStringWithShadow(healthStr, x + 40f + 85f - font1.getStringWidth(healthStr) / 2f + mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 26f, blackcolor2)
        mc.fontRendererObj.drawStringWithShadow("\u2764", x + 40f + 85f - font1.getStringWidth(healthStr) / 2f - mc.fontRendererObj.getStringWidth("\u2764") / 1.9f, y + 26.5f, hurt.rgb)

        val font2 = Hanabi.INSTANCE.fontLoaders.arial14

        if (nulltarget) {
            font2.drawStringWithShadow("XYZ:" + 0 + " " + 0 + " " + 0 + " | " + "Hurt: " + false, x + 37f, y + 15f, Colors.WHITE.c)
            font1.drawStringWithShadow("(No target)", x + 36f, y + 5f, Colors.WHITE.c)
        } else {
            font2.drawStringWithShadow("XYZ:" + target!!.posX + " " + target!!.posY + " " + target!!.posZ + " | " + "Hurt: " + (target!!.hurtTime > 0), x + 37f, y + 15f, Colors.WHITE.c)

            if (target is EntityPlayer) {
                font2.drawStringWithShadow("Block:" + " " + if ((target as EntityPlayer).isBlocking) "True" else "False", x + 37f, y + 25f, Colors.WHITE.c)
            }

            font1.drawStringWithShadow(target!!.name, x + 36f, y + 4f, Colors.WHITE.c)
            if (target is EntityPlayer) {
                GlStateManager.resetColor()
                mc.textureManager.bindTexture((target as AbstractClientPlayer).locationSkin)
                GlStateManager.color(1f, 1f, 1f)
                Gui.drawScaledCustomSizeModalRect(x.toInt() + 3, y.toInt() + 3, 8f, 8f, 8, 8, 32, 32, 64f, 64f)
            }
        }
        GL11.glDisable(3089)
    }

    @EventTarget
    fun onUpdate(event: EventMotion) {
        if (mc.currentScreen != null) return
        setSuffix("Priority")
        onMotion(event)
    }

    @EventTarget
    fun on3D(e: EventRender3D) {
        if (target != null) if (esp.value) {
            drawCircle(target!!, 0.66, true)
        }
    }

    private fun drawCircle(entity: Entity, rad: Double, shade: Boolean) {
        GL11.glPushMatrix()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(770, 771)
        GL11.glHint(3154, 4354)
        GL11.glHint(3155, 4354)
        GL11.glHint(3153, 4354)
        GL11.glDepthMask(false)
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f)
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH)
        GlStateManager.disableCull()
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
        val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX
        val y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY + Math.sin(System.currentTimeMillis() / 2E+2) + 1
        val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ
        var c: Color
        var i = 0f

        while (i < Math.PI * 2) {
            val vecX = x + rad * cos(i)
            val vecZ = z + rad * sin(i)
            c = ColorUtils.intToColor(targetColor.color)
            if (shade) {
                GL11.glColor4f(c.red / 255f, c.green / 255f, c.blue / 255f, 0f)
                GL11.glVertex3d(vecX, y - cos(System.currentTimeMillis() / 2E+2) / 2f, vecZ)
                GL11.glColor4f(c.red / 255f, c.green / 255f, c.blue / 255f, 0.85f)
            }
            GL11.glVertex3d(vecX, y, vecZ)
            i += (Math.PI * 2f / 64f).toFloat()
        }
        GL11.glEnd()
        if (shade) GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f)
        GlStateManager.enableCull()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glDisable(GL11.GL_POINT_SMOOTH)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopMatrix()
        GL11.glColor3f(255f, 255f, 255f)
    }

    private fun onMotion(event: EventMotion) {
        if (event.type == EventType.PRE) {
            val block = !autoBlockValue.current.equals("None", ignoreCase = true)
            when (proiorityValue.current) {
                "Health" -> target =
                    healthPriority

                "Angle" -> target = anglePriority
                "Distance" -> target = getClosest(rangeValue.value)
            }
            if (block) if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword) {
                if (target != null) {
                    if (autoBlockValue.current != "Fake") {
                        doBlock(true)
                    }
                }
                if (target == null) {
                    if (isBlocking) {
                        if (autoBlockValue.current != "Fake") {
                            unBlock(true)
                        }
                    }
                }
            }
            if (target == null) return
            val neededRotations = getNeededRotations(target)
            if (MovementUtils.isMoving() && keepSprintValue.value) mc.thePlayer.isSprinting = true
            if (mc.thePlayer.getDistanceToEntity(target) <= rangeValue.value) {
                event.setPitch(neededRotations[1])
                event.setYaw(neededRotations[0])

                val requiredTime = 1000.0 / desiredCps
                if (attackTimer.hasReached(requiredTime)) {
                    isBlocking = false
                    mc.thePlayer.swingItem()
                    mc.playerController.attackEntity(mc.thePlayer, target)
                    desiredCps = MathUtils.getRandomInRange(minCPSValue.value, maxCPSValue.value)
                    attackTimer.reset()
                }
            } else {
                target = null
            }
        } else if (event.type == EventType.POST) {
            if (autoBlockValue.current == "Watchdog") {
                if (target == null) {
                    if (mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword) {
                        mc.gameSettings.keyBindUseItem.pressed = false
                    }
                }
            }
        }
    }

    val healthPriority: EntityLivingBase?
        get() {
            val entities: MutableList<EntityLivingBase> = ArrayList()
            for (e in mc.theWorld.loadedEntityList) {
                if (e !is EntityLivingBase) continue
                if (mc.thePlayer.getDistanceToEntity(e) < rangeValue.value.toFloat() && isValid(e)) entities.add(e)
            }
            entities.sortWith { o1: EntityLivingBase, o2: EntityLivingBase -> (o1.health - o2.health).toInt() }
            return if (entities.isEmpty()) null else entities[0]
        }
    val anglePriority: EntityLivingBase?
        get() {
            val entities: MutableList<EntityLivingBase> = ArrayList()
            for (e in mc.theWorld.loadedEntityList) {
                if (e !is EntityLivingBase) continue
                val player = e
                if (mc.thePlayer.getDistanceToEntity(player) < rangeValue.value.toFloat() && isValid(player)) {
                    entities.add(player)
                }
            }
            entities.sortWith { o1: EntityLivingBase?, o2: EntityLivingBase? ->
                val rot1 = getNeededRotations(o1)
                val rot2 = getNeededRotations(o2)
                (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0])).toInt()
            }
            return if (entities.isEmpty()) null else entities[0]
        }
    var timerUtil = TimerUtil()

    @EventTarget
    fun onPacket(e: EventPacketReceived) {
        if (e.packet is S08PacketPlayerPosLook && target == null && timerUtil.hasReached(13000.0)) {
            unBlock(true)
            timerUtil.reset()
        }
    }

    private fun getClosest(rangeProperty: Double): EntityLivingBase? {
        var dist = rangeProperty
        var target: EntityLivingBase? = null
        for (ent in mc.theWorld.loadedEntityList) {
            if (ent !is EntityLivingBase) continue
            if (!isValid(ent)) continue
            val currentDist = mc.thePlayer.getDistanceToEntity(ent).toDouble()
            if (currentDist <= dist) {
                dist = currentDist
                target = ent
            }
        }
        return target
    }

    companion object {
        var autoBlockValue = ModeValue("Auto Block", "Watchdog", "Watchdog", "Fake", "Interact", "Packet", "None")
        var playerValue = BooleanValue("Player", true)
        var mobValue = BooleanValue("Mob", false)
        var animalValue = BooleanValue("Animal", false)
        var teamValue = BooleanValue("Teams", true)
        var invisible = BooleanValue("Invisible", true)
        var onlyOnAim = BooleanValue("OnlyOnAim", false)
        @JvmField
        var target: EntityLivingBase? = null

        fun getAnimationStateSmooth(target: Double, current: Double, speed: Double): Double {
            var current = current
            var speed = speed
            val larger = target > current
            if (speed < 0.0) {
                speed = 0.0
            } else if (speed > 1.0) {
                speed = 1.0
            }
            if (target == current) {
                return target
            }
            val dif = Math.max(target, current) - Math.min(target, current)
            var factor = dif * speed
            if (factor < 0.1) {
                factor = 0.1
            }
            if (larger) {
                if (current + factor > target) {
                    current = target
                } else {
                    current += factor
                }
            } else {
                if (current - factor < target) {
                    current = target
                } else {
                    current -= factor
                }
            }
            return current
        }

        fun isValid(entity: EntityLivingBase): Boolean {
            if (entity == mc.thePlayer || !entity.isEntityAlive) return false
            if (Teams.isOnSameTeam(entity)) return false
            if (entity is EntityPlayer && !playerValue.value) return false
            if ((entity is EntityMob || entity is EntityVillager || entity is EntitySquid) && !mobValue.value) return false
            if (entity is EntityAnimal && !animalValue.value) return false
            if (isTeamMate(entity) && !teamValue.value) return false
            return if (isBot(entity)) false else !entity.isInvisible || !invisible.value
        }

        private fun isTeamMate(entity: EntityLivingBase): Boolean {
            if (entity.team != null && mc.thePlayer.team != null) {
                val first = entity.displayName.formattedText[1]
                val second = mc.thePlayer.displayName.formattedText[1]
                return first == second
            }
            return false
        }

        fun getNeededRotations(entityIn: EntityLivingBase?): FloatArray {
            val d0 = entityIn!!.posX - mc.thePlayer.posX
            val d1 = entityIn.posZ - mc.thePlayer.posZ
            val d2 = entityIn.posY + entityIn.eyeHeight - (mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.eyeHeight)
            val d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1).toDouble()
            val f = (MathHelper.atan2(d1, d0) * 180.0 / Math.PI).toFloat() - 90.0f
            val f1 = (-(MathHelper.atan2(d2, d3) * 180.0 / Math.PI)).toFloat()
            return floatArrayOf(f, f1)
        }

        fun getNeededRotations(from: EntityLivingBase, to: EntityLivingBase): FloatArray {
            val d0 = to.posX - from.posX
            val d1 = to.posZ - from.posZ
            val d2 = to.posY + to.eyeHeight - (mc.thePlayer.entityBoundingBox.minY + from.eyeHeight)
            val d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1).toDouble()
            val f = (MathHelper.atan2(d1, d0) * 180.0 / Math.PI).toFloat() - 90.0f
            val f1 = (-(MathHelper.atan2(d2, d3) * 180.0 / Math.PI)).toFloat()
            return floatArrayOf(f, f1)
        }
    }
}