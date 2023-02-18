package life.hanabi.modules.render

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.utils.NetworkUtils
import life.hanabi.utils.render.BlurBuffer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.scoreboard.Score
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.EnumChatFormatting
import java.awt.Color
import java.util.stream.Collectors

class Scoreboard(name: String) : Module(name, ModuleCategory.Render) {
    var bgColor = ColorValue("Background Color", "Scoreboard background color.", Color(0, 0, 0, 150).rgb)

    init {
        addValues(background, blur, numbers, bgColor)
    }

    override fun onGui() {
        super.onGui()
        val scaledresolution = ScaledResolution(mc)
        val scoreboard: Scoreboard = mc.theWorld.scoreboard
        var scoreobjective: ScoreObjective? = null
        val scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.name)

        if (scoreplayerteam != null) {
            val i1 = scoreplayerteam.chatFormat.colorIndex
            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1)
            }
        }

        val scoreobjective1 = scoreobjective ?: scoreboard.getObjectiveInDisplaySlot(1)

        if (scoreobjective1 != null) {
            renderScoreboard(scoreobjective1, scaledresolution)
        }
    }

    private fun renderScoreboard(objective: ScoreObjective, scaledRes: ScaledResolution) {
        val scoreboard = objective.scoreboard
        var collection = scoreboard.getSortedScores(objective)
        val list = collection.stream().filter { p_apply_1_: Score ->
            p_apply_1_.playerName != null && !p_apply_1_.playerName.startsWith("#")
        }.collect(Collectors.toList())
        collection = if (list.size > 15) {
            Lists.newArrayList(Iterables.skip(list, collection.size - 15))
        } else {
            list
        }
        var i = mc.fontRendererObj.getStringWidth(objective.displayName)
        for (score in collection) {
            val scoreplayerteam = scoreboard.getPlayersTeam(score.playerName)
            val s = ScorePlayerTeam.formatPlayerName(
                scoreplayerteam,
                score.playerName
            ) + ": " + EnumChatFormatting.RED + score.scorePoints
            i = Math.max(i, mc.fontRendererObj.getStringWidth(s))
        }
        val i1 = collection.size * mc.fontRendererObj.FONT_HEIGHT
        val j1 = y * scaledRes.scaledHeight + i1 + 8
        val l1 = x * scaledRes.scaledWidth
        var j = 0
        if (y == 0f) {
            y = (scaledRes.scaledHeight / 2f + i1 / 3f) / scaledRes.scaledHeight
        }
        width = i.toFloat()
        height = (i1 + 8).toFloat()
        for (score1 in collection) {
            ++j
            val scoreplayerteam1 = scoreboard.getPlayersTeam(score1.playerName)
            val s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.playerName)
            val s2 = EnumChatFormatting.RED.toString() + "" + score1.scorePoints
            val k = (j1 - j * mc.fontRendererObj.FONT_HEIGHT).toInt()
            val l = (l1 + i + 2).toInt()
            if (background.value) {
                if (blur.value) {
                    BlurBuffer.blurArea(l1 - 2, k.toFloat(), l.toFloat(), (k + mc.fontRendererObj.FONT_HEIGHT).toFloat(), true)
                }
                Gui.drawRect(l1 - 2, k, l.toFloat(), k + mc.fontRendererObj.FONT_HEIGHT, 1342177280)
            }
            if (NetworkUtils.isOnHypixel()) {
                mc.fontRendererObj.drawString(
                    s1.replace("\uD83C\uDF81".toRegex(), "")
                        .replace("\uD83D\uDC79".toRegex(), "")
                        .replace("\uD83C\uDFC0".toRegex(), "")
                        .replace("⚽".toRegex(), "")
                        .replace("\uD83C\uDF6D".toRegex(), "")
                        .replace("\uD83C\uDF20".toRegex(), "")
                        .replace("\uD83D\uDC7E".toRegex(), "")
                        .replace("\uD83D\uDC0D".toRegex(), "")
                        .replace("\uD83D\uDD2E".toRegex(), "")
                        .replace("\uD83D\uDC7D".toRegex(), "")
                        .replace("\uD83D\uDCA3".toRegex(), "")
                        .replace("\uD83C\uDF6B".toRegex(), "")
                        .replace("\uD83D\uDD2B".toRegex(), "")
                        .replace("\uD83C\uDF82".toRegex(), "")
                        .replace("\uD83C\uDF89".toRegex(), ""), l1, k.toFloat(), -1
                )
            } else {
                mc.fontRendererObj.drawString(s1, l1, k.toFloat(), -1)
            }
            if (numbers.value) {
                mc.fontRendererObj.drawString(s2, (l - mc.fontRendererObj.getStringWidth(s2)).toFloat(), k.toFloat(), -1)
            }
            if (j == collection.size) {
                val s3 = objective.displayName
                if (background.value) {
                    if (blur.value) {
                        BlurBuffer.blurArea(l1 - 2, (k - mc.fontRendererObj.FONT_HEIGHT - 1).toFloat(), l.toFloat(), (k - 1).toFloat(), true)
                        BlurBuffer.blurArea(l1 - 2, (k - 1).toFloat(), l.toFloat(), k.toFloat(), true)
                        Gui.drawRect(l1 - 2, k - mc.fontRendererObj.FONT_HEIGHT - 1, l.toFloat(), k - 1, 1610612736)
                        Gui.drawRect(l1 - 2, k - 1, l.toFloat(), k, 1342177280)
                    } else {
                        Gui.drawRect(l1 - 2, k - mc.fontRendererObj.FONT_HEIGHT - 1, l.toFloat(), k - 1, 1610612736)
                        Gui.drawRect(l1 - 2, k - 1, l.toFloat(), k, 1342177280)
                    }
                }
                mc.fontRendererObj.drawString(s3, l1 + i / 2f - mc.fontRendererObj.getStringWidth(s3) / 2f, (k - mc.fontRendererObj.FONT_HEIGHT).toFloat(), -1)
            }
        }
    }

    companion object {
        var background = BooleanValue("BackGround", "Scoreboard background.", true)
        var blur = BooleanValue("Blur", "Scoreboard blur background.", true)
        var numbers = BooleanValue("Numbers", "Scoreboard red numbers.", false)
    }
}