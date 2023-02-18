package life.hanabi.modules.render

import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.EventRender2D
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.gui.font.UFontRenderer
import life.hanabi.utils.Colors
import life.hanabi.utils.RotationUtil
import life.hanabi.utils.math.ColorUtils
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

class Nametags(name: String) : Module(name, ModuleCategory.Render) {
    var invis = BooleanValue("Invisible", "Invisible", false)
    var armor = BooleanValue("Armor", "Armor", false)

    init {
        addValues(invis, armor)
    }

    override fun onEnable() {
        super.onEnable()
        TOGGLE = true
    }

    override fun onDisable() {
        super.onDisable()
        TOGGLE = false
    }

    @EventTarget
    fun update(event: EventRender3D) {
        updatePositions()
    }

    @EventTarget
    fun onRender2D(event: EventRender2D) {
        val scaledRes = ScaledResolution(mc)
        try {
            for (ent in entityPositions.keys) {
                if (ent != mc.thePlayer && (invis.value || !ent.isInvisible)) {
                    GlStateManager.pushMatrix()
                    if (ent is EntityPlayer) {
                        val renderPositions = entityPositions[ent]
                        if (renderPositions!![3] < 0.0 || renderPositions[3] >= 1.0) {
                            GlStateManager.popMatrix()
                            continue
                        }
                        val font = Hanabi.INSTANCE.fontLoaders.default16
                        GlStateManager.translate(renderPositions[0] / scaledRes.scaleFactor, renderPositions[1] / scaledRes.scaleFactor, 0.0)
                        GlStateManager.scale(1f, 1f, 1f)
                        GlStateManager.translate(0.0, -2.5, 0.0)
                        val str = ent.getName()
                        val allWidth = (font.getStringWidth(str.replace("\u00a7.".toRegex(), "")) + 14).toFloat()
                        RenderUtil.drawRect(-allWidth / 2, -14.0f, allWidth / 2, 0f, Colors.getColor(0, 150))
                        font.drawString(
                            str.replace("\u00a7.".toRegex(), ""),
                            -allWidth / 2 + 5.5f,
                            -13f,
                            Colors.WHITE.c
                        )
                        val nowhealth = ceil((ent.getHealth() + ent.getAbsorptionAmount()).toDouble()).toFloat()
                        val maxHealth = ent.getMaxHealth() + ent.getAbsorptionAmount()
                        val healthP = nowhealth / maxHealth
                        var color = Colors.RED.c
                        var text = ent.getDisplayName().formattedText

                        //Megawalls
                        text = text.replace((if (text.contains("[") && text.contains("]")) "\u00a77" else "").toRegex(), "")

                        for (i in 0 until text.length) {
                            if (text[i] == '\u00a7' && i + 1 < text.length) {
                                val oneMore = text[i + 1].lowercaseChar()
                                val colorCode = "0123456789abcdefklmnorg".indexOf(oneMore)
                                if (colorCode < 16) {
                                    try {
                                        color = ColorUtils.reAlpha(mc.fontRendererObj.getColorCode(oneMore), 1f)
                                    } catch (ignored: ArrayIndexOutOfBoundsException) {
                                    }
                                }
                            }
                        }
                        RenderUtil.drawRect(-allWidth / 2, -2f, allWidth / 2 - allWidth / 2 * (1 - healthP) * 2, 0f, ColorUtils.reAlpha(color, 0.8f))
                        val armors = armor.value
                        if (armors) {
                            val itemsToRender: MutableList<ItemStack> = ArrayList()
                            for (i in 0..4) {
                                val stack = ent.getEquipmentInSlot(i)
                                if (stack != null) {
                                    itemsToRender.add(stack)
                                }
                            }
                            var x = -(itemsToRender.size * 9) - 3
                            for (stack in itemsToRender) {
                                GlStateManager.pushMatrix()
                                RenderHelper.enableGUIStandardItemLighting()
                                GlStateManager.disableAlpha()
                                GlStateManager.clear(256)
                                mc.renderItem.zLevel = -150.0f
                                fixGlintShit()
                                mc.renderItem.renderItemIntoGUI(stack, x + 6, -32)
                                mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, x + 6, -32)
                                mc.renderItem.zLevel = 0.0f
                                x += 6
                                GlStateManager.enableAlpha()
                                RenderHelper.disableStandardItemLighting()
                                GlStateManager.popMatrix()
                                var y = 0
                                val sLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack)
                                val fLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack)
                                val kLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack
                                )
                                if (sLevel > 0) {
                                    drawEnchantTag("Sh" + getColor(sLevel) + sLevel, x.toFloat(), y.toFloat())
                                    y += UFontRenderer.FONT_HEIGHT - 2
                                }
                                if (fLevel > 0) {
                                    drawEnchantTag("Fir" + getColor(fLevel) + fLevel, x.toFloat(), y.toFloat())
                                    y += UFontRenderer.FONT_HEIGHT - 2
                                }
                                if (kLevel > 0) {
                                    drawEnchantTag("Kb" + getColor(kLevel) + kLevel, x.toFloat(), y.toFloat())
                                } else if (stack.item is ItemArmor) {
                                    val pLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)
                                    val tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack)
                                    val uLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)

                                    if (pLevel > 0) {
                                        drawEnchantTag("P" + getColor(pLevel) + pLevel, x.toFloat(), y.toFloat())
                                        y += UFontRenderer.FONT_HEIGHT - 2
                                    }
                                    if (tLevel > 0) {
                                        drawEnchantTag("Th" + getColor(tLevel) + tLevel, x.toFloat(), y.toFloat())
                                        y += UFontRenderer.FONT_HEIGHT - 2
                                    }
                                    if (uLevel > 0) {
                                        drawEnchantTag("Unb" + getColor(uLevel) + uLevel, x.toFloat(), y.toFloat())
                                    }
                                } else if (stack.item is ItemBow) {
                                    val powLevel =
                                        EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack)
                                    val punLevel =
                                        EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack)
                                    val fireLevel =
                                        EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack)

                                    if (powLevel > 0) {
                                        drawEnchantTag("Pow" + getColor(powLevel) + powLevel, x.toFloat(), y.toFloat())
                                        y += UFontRenderer.FONT_HEIGHT - 2
                                    }
                                    if (punLevel > 0) {
                                        drawEnchantTag("Pun" + getColor(punLevel) + punLevel, x.toFloat(), y.toFloat())
                                        y += UFontRenderer.FONT_HEIGHT - 2
                                    }
                                    if (fireLevel > 0) {
                                        drawEnchantTag("Fir" + getColor(fireLevel) + fireLevel, x.toFloat(), y.toFloat())
                                    }
                                } else if (stack.rarity == EnumRarity.EPIC) {
                                    drawEnchantTag("\u00a76\u00a7lGod", x - 0.5f, (y + 12).toFloat())
                                }
                                x += 12
                            }
                        }
                    }
                    GlStateManager.popMatrix()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun fixGlintShit() {
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableBlend()
        GlStateManager.enableLighting()
        GlStateManager.enableDepth()
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()
        GlStateManager.disableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.enableTexture2D()
        GlStateManager.enableLighting()
        GlStateManager.enableDepth()
    }

    private fun getColor(level: Int): String {
        if (level == 2) {
            return "\u00a7a"
        } else if (level == 3) {
            return "\u00a73"
        } else if (level == 4) {
            return "\u00a74"
        } else if (level >= 5) {
            return "\u00a76"
        }
        return "\u00a7f"
    }

    private fun drawEnchantTag(text: String, x: Float, y: Float) {
        var x = x
        var y = y
        GlStateManager.pushMatrix()
        GlStateManager.disableDepth()
        x = (x * 1.05).toInt().toFloat()
        y -= 6f
        Hanabi.INSTANCE.fontLoaders.default14.drawString(text, x, -44 - y, Colors.WHITE.c)
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }

    private fun updatePositions() {
        entityPositions.clear()
        val pTicks = mc.timer.renderPartialTicks
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity != mc.thePlayer && entity is EntityPlayer && (!entity.isInvisible() || invis.value)) {
                val x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * pTicks - RenderManager.renderPosX
                var y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * pTicks - RenderManager.renderPosY
                val z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * pTicks - RenderManager.renderPosZ
                y += entity.height + 0.25
                //                System.out.println(Objects.requireNonNull(convertTo2D(x, y, z))[2]);
//                if ((Objects.requireNonNull(convertTo2D(x, y, z))[2] >= 0.0D) && (Objects.requireNonNull(convertTo2D(x, y, z))[2] < 1.0D)) {
                entityPositions[entity] = doubleArrayOf(Objects.requireNonNull(convertTo2D(x, y, z))!![0], Objects.requireNonNull(convertTo2D(x, y, z))!![1], abs(convertTo2D(x, y + 1.0, z, entity)!![1] - convertTo2D(x, y, z, entity)!![1]), Objects.requireNonNull(convertTo2D(x, y, z))!![2])
            }
        }
    }

    private fun convertTo2D(x: Double, y: Double, z: Double, ent: Entity): DoubleArray? {
        val pTicks = mc.timer.renderPartialTicks
        val prevYaw = mc.thePlayer.rotationYaw
        val prevPrevYaw = mc.thePlayer.prevRotationYaw
        val rotations = RotationUtil.getRotationFromPosition(
            ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks,
            ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks,
            ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - 1.6
        )
        mc.renderViewEntity.rotationYaw = rotations[0].also {
            mc.renderViewEntity.prevRotationYaw = it
        }
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(pTicks, 0)
        val convertedPoints = convertTo2D(x, y, z)
        mc.renderViewEntity.rotationYaw = prevYaw
        mc.renderViewEntity.prevRotationYaw = prevPrevYaw
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(pTicks, 0)
        return convertedPoints
    }

    private fun convertTo2D(x: Double, y: Double, z: Double): DoubleArray? {
        val screenCoords = BufferUtils.createFloatBuffer(3)
        val viewport = BufferUtils.createIntBuffer(16)
        val modelView = BufferUtils.createFloatBuffer(16)
        val projection = BufferUtils.createFloatBuffer(16)
        GL11.glGetFloat(2982, modelView)
        GL11.glGetFloat(2983, projection)
        GL11.glGetInteger(2978, viewport)
        val result = GLU.gluProject(x.toFloat(), y.toFloat(), z.toFloat(), modelView, projection, viewport, screenCoords)

        return if (result) { doubleArrayOf(screenCoords[0].toDouble(), (Display.getHeight() - screenCoords[1]).toDouble(), screenCoords[2].toDouble())
        } else null
    }

    companion object {
        var entityPositions: MutableMap<EntityLivingBase, DoubleArray> = HashMap()
        @JvmField
        var TOGGLE = false
    }
}