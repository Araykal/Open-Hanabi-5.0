package life.hanabi.gui.notification

import life.hanabi.Hanabi
import life.hanabi.core.Module.mc
import life.hanabi.utils.math.AnimationUtils
import life.hanabi.utils.math.TimerUtil
import life.hanabi.utils.render.RenderUtil
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation
import java.awt.Color

open class Notification(var text: String, var type: Type) {
    var timer: Boolean = false
    var width: Double = 150.0
    var height = 20.0
    var x: Float = 50f
    var y = 0f
    var position = 0f
    var `in` = true
    var animationUtils = AnimationUtils()
    var yAnimationUtils = AnimationUtils()
    var timerUtil = TimerUtil()
    var alphaAnimationUtils = AnimationUtils()
    var alpha: Float = 0f

    init {
        width = (80 + Hanabi.INSTANCE.fontLoaders!!.default18.getStringWidth(text)).toDouble();
    }

    open fun onRender() {
        if (timerUtil.delay(1500F)) {
            timer = true
            timerUtil.reset()
        }
        var i = 0
        for (notification in Hanabi.INSTANCE.notificationsManager!!.notifications) {
            if (notification === this) {
                break
            }
            if (notification !is Info)
                i++
        }
        val color = Color(30,30,30)
        y = yAnimationUtils.animate((i.toFloat() * (height + 5)).toFloat(), y, 0.2f, true)
        if(`in`) {
            alpha = alphaAnimationUtils.animate(255f, alpha, 0.1f, true)
        }else{
            alpha = alphaAnimationUtils.animate(0f, alpha, 0.1f, true)
        }

        val sr = ScaledResolution(mc)
        RenderUtil.drawRoundRect5(
            (sr.scaledWidth + x - width).toFloat(),
            sr.scaledHeight - 50f - y - 20,
            (width - 10).toFloat(),
            22f,
            Color(
                color.red,
                color.green,
                color.blue,
                alpha.toInt()
            )
        )

        RenderUtil.drawImage(
            ResourceLocation("client/guis/notification/" + type.name + ".png"),
            (sr.scaledWidth + x - width).toFloat() + 6,
            sr.scaledHeight - 50f - y - 14,
            12f,
            12f,
            Color(
                255,
                255,
                255,
                alpha.toInt()
            )
        )
        Hanabi.INSTANCE.fontLoaders!!.syFont18.drawString(
            text,
            (sr.scaledWidth + x - width + 24).toFloat(),
            sr.scaledHeight - 50f - y - 12,
            Color(
                255,
                255,
                255,
                alpha.toInt()
            ).rgb
        )
    }

    enum class Type(color: Color) {
        Success(Color(89, 255, 180)),
        Error(Color(255, 40, 50)),
        Info(Color(0, 132, 255)),
        Warning(Color(255, 205, 100));

        var color: Int

        init {
            this.color = color.rgb
        }
    }
}