package life.hanabi.modules.render

import com.mojang.realmsclient.gui.ChatFormatting
import life.hanabi.Hanabi
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.cloudmusic.MusicManager
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.render.EventRender2D
import life.hanabi.gui.font.UFontRenderer
import life.hanabi.utils.math.AnimationUtils
import life.hanabi.utils.math.ColorUtils
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import java.awt.Color

class HUD(name: String) :
    Module(name, ModuleCategory.Render) {

    var showLogo = BooleanValue("ShowLogo", false)
    var arraylist = BooleanValue("ArrayList", true)
    var noRender = BooleanValue("NoRender", true)
    var shadow = BooleanValue("ArrayListShadow", true)
    var rect = BooleanValue("Rect", true)
    var rectColor = ColorValue("RectColor", Hanabi.INSTANCE.theme.themeColor.rgb)
    var color = ColorValue("Color", -1)
    var information = ColorValue("Information", -1)
    private var alphaAnimation = 0f
    private var yAxisAnimation = 0f

    init {
        addValues(showLogo, arraylist, shadow, color, information, noRender, rect, rectColor)
    }

    var listY = 0f;
    var anim: AnimationUtils = AnimationUtils()

    @EventTarget
    fun onRender(e: EventRender2D) {
        val sr = ScaledResolution(mc);
        if (showLogo.value) {
            RenderUtil.drawImage(ResourceLocation("client/logo.png"), 5F, 5F, 39F, 44F, Color(color.color))
        }
        val font: UFontRenderer = Hanabi.INSTANCE.fontLoaders.syFont18
        val tFont: UFontRenderer = Hanabi.INSTANCE.fontLoaders.syFont16


        if (MusicManager.INSTANCE.mediaPlayer != null) {
            val mill: Long = MusicManager.INSTANCE.mediaPlayer.getCurrentTime().toMillis().toLong()
            if (!MusicManager.INSTANCE.lrc.isEmpty()) {
                if (MusicManager.INSTANCE.lrc.get(MusicManager.INSTANCE.lrcIndex).time < mill) {
                    if (MusicManager.INSTANCE.currentLyrics.isEmpty()) {
                        if (MusicManager.INSTANCE.lrc.size - 1 > MusicManager.INSTANCE.lrcIndex - 2 && MusicManager.INSTANCE.lrcIndex - 2 >= 0) MusicManager.INSTANCE.currentLyrics.add(
                            MusicManager.INSTANCE.lrc.get(
                                MusicManager.INSTANCE.lrcIndex - 2
                            )
                        )
                        if (MusicManager.INSTANCE.lrc.size - 1 > MusicManager.INSTANCE.lrcIndex - 1 && MusicManager.INSTANCE.lrcIndex - 1 >= 0) MusicManager.INSTANCE.currentLyrics.add(
                            MusicManager.INSTANCE.lrc.get(
                                MusicManager.INSTANCE.lrcIndex - 1
                            )
                        )
                        MusicManager.INSTANCE.currentLyrics.add(MusicManager.INSTANCE.lrc.get(MusicManager.INSTANCE.lrcIndex))
                        if (MusicManager.INSTANCE.lrc.size - 1 > MusicManager.INSTANCE.lrcIndex + 1) MusicManager.INSTANCE.currentLyrics.add(
                            MusicManager.INSTANCE.lrc.get(MusicManager.INSTANCE.lrcIndex + 1)
                        )
                        if (MusicManager.INSTANCE.lrc.size - 1 > MusicManager.INSTANCE.lrcIndex + 2) MusicManager.INSTANCE.currentLyrics.add(
                            MusicManager.INSTANCE.lrc.get(MusicManager.INSTANCE.lrcIndex + 2)
                        )
                        if (MusicManager.INSTANCE.lrc.size - 1 > MusicManager.INSTANCE.lrcIndex + 1) {
                            MusicManager.INSTANCE.lrcIndex += 1
                        }
                    } else {
                        MusicManager.INSTANCE.currentLyrics.removeAt(0)
                    }
                }
            }


            var y = 5F
            if (MusicManager.INSTANCE.currentLyrics.size > 0) {
                for (i in 1.coerceAtMost(MusicManager.INSTANCE.currentLyrics.size)..if (MusicManager.INSTANCE.currentLyrics.size > 2) MusicManager.INSTANCE.currentLyrics.size - 2 else 0) {
                    val currentLyric = MusicManager.INSTANCE.currentLyrics[i]
                    val lyric = currentLyric.text
                    val lyricHeight = 12
                    if (currentLyric.y == 0f) {
                        currentLyric.y = y + 60;
                    }
                    currentLyric.y = currentLyric.animationUtils.animate(y, currentLyric.y, 0.2f)
                    val lyricY = sr.scaledHeight - 140 - lyricHeight / 2 + currentLyric.y
                    if (i == 2) {
                        val lyricWidth = Hanabi.INSTANCE.fontLoaders.syFont20.getStringWidth(lyric)
                        val lyricX = sr.scaledWidth / 2 - lyricWidth / 2
                        currentLyric.progress = currentLyric.animationUtils2.animate(1f, currentLyric.progress, 0.1f)
                        Hanabi.INSTANCE.fontLoaders.syFont20.drawString(
                            lyric,
                            lyricX.toFloat(),
                            lyricY.toFloat(),
                            ColorUtils.reAlpha(Color.WHITE.rgb, currentLyric.progress)
                        )
                    } else {
                        val lyricWidth = font.getStringWidth(lyric)
                        val lyricX = sr.scaledWidth / 2 - lyricWidth / 2
                        currentLyric.progress = currentLyric.animationUtils2.animate(0.7f, currentLyric.progress, 0.1f)
                        font.drawString(
                            lyric,
                            lyricX.toFloat(),
                            lyricY.toFloat(),
                            ColorUtils.reAlpha(Color.WHITE.rgb, currentLyric.progress)
                        )
                    }
                    if (currentLyric.tText != null) {
                        val lyricX = sr.scaledWidth / 2 - font.getStringWidth(currentLyric.tText) / 2

                        tFont.drawString(
                            currentLyric.tText,
                            lyricX.toFloat(),
                            lyricY.toFloat() + 12,
                            ColorUtils.reAlpha(Color.WHITE.rgb, currentLyric.progress)
                        )
                        y += 10;
                    }
                    y += 20
                }
            }
        }

        if (arraylist.value) {
            val sortedBy =
                ModuleManager.modules.values.sortedBy { -font.getStringWidth(it.name + " " + if (it.displayName == null) "" else it.displayName) }
            var yPos: Float = listY
            var notiY = 0f
            for (notification in Hanabi.INSTANCE.notificationsManager.notifications) {
                if (notification is life.hanabi.gui.notification.Info)
                    notiY += notification.height.toFloat() + 20f
            }
            listY = anim.animate(notiY, listY, 0.2f)

            for (it in sortedBy) {
                if (noRender.value && it.type.equals(ModuleCategory.Render))
                    continue
                val s = it.name + " " + ChatFormatting.GRAY + if (it.displayName == null) "" else it.displayName

                if (it.arrayListAnim2 > 40) {
                    if (rect.value) {
                        RenderUtil.drawRect(
                            it.arrayListAnim.toFloat(),
                            it.arrayListAnim3.toFloat(),
                            sr.scaledWidth.toFloat(),
                            it.arrayListAnim3.toFloat() + 12,
                            ColorUtils.reAlpha(
                                Color(0, 0, 0).rgb,
                                1f.coerceAtMost(0f.coerceAtLeast((it.arrayListAnim2 / 255).toFloat())) * 0.5f
                            )
                        )
                        RenderUtil.drawRect(
                            it.arrayListAnim.toFloat(),
                            it.arrayListAnim3.toFloat(),
                            it.arrayListAnim.toFloat() + 1,
                            it.arrayListAnim3.toFloat() + 12,
                            ColorUtils.reAlpha(
                                rectColor.color,
                                1f.coerceAtMost(0f.coerceAtLeast((it.arrayListAnim2 / 255).toFloat()))
                            )
                        )
                    }

                    if (shadow.value)
                        font.drawStringWithNewShadow(
                            s,
                            it.arrayListAnim.toFloat() + 4,
                            it.arrayListAnim3.toFloat() + 1,
//                            color.color
                            ColorUtils.reAlpha(
                                color.color,
                                1f.coerceAtMost(0f.coerceAtLeast((it.arrayListAnim2 / 255).toFloat()))
                            )
                        )
                    else
                        font.drawStringWithShadow(
                            s,
                            it.arrayListAnim.toFloat() + 4,
                            it.arrayListAnim3.toFloat() + 1,
//                            color.color
                            ColorUtils.reAlpha(
                                color.color,
                                1f.coerceAtMost(0f.coerceAtLeast((it.arrayListAnim2 / 255).toFloat()))
                            )
                        )
                }
                if (it.stage) {
                    it.arrayListAnim = it.arrayListA.animate(
                        (sr.scaledWidth - font.getStringWidth(s) - 6).toDouble(),
                        it.arrayListAnim,
                        0.2
                    )
                    it.arrayListAnim3 = it.arrayListC.animate(
                        yPos.toDouble(),
                        it.arrayListAnim3,
                        0.2
                    )
                    it.arrayListAnim2 = it.arrayListB.animate(255.0, it.arrayListAnim2, 0.2)
                } else {
                    it.arrayListAnim = it.arrayListA.animate(sr.scaledWidth.toDouble(), it.arrayListAnim, 0.2)
                    it.arrayListAnim2 = it.arrayListB.animate(0.0, it.arrayListAnim2, 0.2)
                }
                if (it.arrayListAnim2 > 30) {
                    yPos += 12
                }
            }
        }

        var posY = 0;
        if (Minecraft.getMinecraft().currentScreen is GuiChat) {
            posY = 14;
        }
        val s1 = "FPS:${Minecraft.getDebugFPS()}" +
                " HurtTime:" + mc.thePlayer.hurtTime
        val s2 =
            "X:${mc.thePlayer.posX.toInt()} Y:${mc.thePlayer.posY.toInt()} Z:${mc.thePlayer.posZ.toInt()} Yaw:${mc.thePlayer.rotationYaw.toInt()} Pitch:${mc.thePlayer.rotationPitch.toInt()}"
        if (rect.value) {
            RenderUtil.drawRect(
                0F,
                (sr.scaledHeight - 24 - posY).toFloat() - 4,
                font.getStringWidth(s1)
                    .coerceAtLeast(font.getStringWidth(s2)) + 20F,
                sr.scaledHeight.toFloat() - posY,
                Color(0, 0, 0, 100).rgb
            )
            RenderUtil.drawRect(
                0F,
                (sr.scaledHeight - 24 - posY).toFloat() - 4,
                font.getStringWidth(s1)
                    .coerceAtLeast(font.getStringWidth(s2)) + 20F,
                (sr.scaledHeight - 24 - posY).toFloat() - 3,
                rectColor.color
            )
        }
        font.drawStringWithNewShadow(
            s1,
            4f,
            sr.scaledHeight - 24 - posY,
            information.color
        )
        font.drawStringWithNewShadow(
            s2,
            4f,
            sr.scaledHeight - 12 - posY,
            information.color
        )

        font.drawStringWithNewShadow(
            Hanabi.CLIENT_NAME + " " + Hanabi.VERSION,
            sr.scaledWidth - font.getStringWidth(Hanabi.CLIENT_NAME + " " + Hanabi.VERSION) - 4f,
            sr.scaledHeight - 14 - posY,
            information.color
        )
        //Scaffold esp
        renderBlockCount(sr.scaledWidth.toFloat(), sr.scaledHeight.toFloat())

    }

    //get blocks count in inventory
    fun getBlockCount(): Int {
        var count = 0
        for (i in 0..44) {
            val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack
            if (itemStack != null && itemStack.item is ItemBlock) {
                count += itemStack.stackSize
            }
        }
        return count
    }


    fun renderBlockCount(width: Float, height: Float) {
        val state = ModuleManager.modules["Scaffold"]!!.stage
        if (state) {
            val blockCount = getBlockCount()
            val syFont18 = Hanabi.INSTANCE.fontLoaders.syFont18
            val fwidth = syFont18.getStringWidth("Blocks: $blockCount")
            syFont18.drawStringWithNewShadow(
                "Blocks: $blockCount",
                (width - fwidth) / 2,
                height / 2 + 30,
                Color.WHITE.rgb
            )
        }
    }


    fun drawArrowRect(left: Float, top: Float, right: Float, bottom: Float, color: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        var e: Float
        if (left < right) {
            e = left
            left = right
            right = e
        }
        if (top < bottom) {
            e = top
            top = bottom
            bottom = e
        }
        val a = (color shr 24 and 255).toFloat() / 255.0f
        val b = (color shr 16 and 255).toFloat() / 255.0f
        val c = (color shr 8 and 255).toFloat() / 255.0f
        val d = (color and 255).toFloat() / 255.0f
        val tes = Tessellator.getInstance()
        val bufferBuilder = Tessellator.getInstance().worldRenderer
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(b, c, d, a)
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION)
        bufferBuilder.pos((left - 5).toDouble(), bottom.toDouble(), 0.0).endVertex()
        bufferBuilder.pos((right + 5).toDouble(), bottom.toDouble(), 0.0).endVertex()
        bufferBuilder.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
        bufferBuilder.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
        Tessellator.getInstance().draw()
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

}
