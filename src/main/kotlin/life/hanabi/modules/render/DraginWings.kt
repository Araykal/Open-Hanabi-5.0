package life.hanabi.modules.render

import life.hanabi.core.Module
import life.hanabi.core.ModuleCategory
import life.hanabi.core.managers.ModuleManager
import life.hanabi.core.values.values.BooleanValue
import life.hanabi.core.values.values.ColorValue
import life.hanabi.core.values.values.NumberValue
import life.hanabi.event.EventManager
import life.hanabi.event.EventTarget
import life.hanabi.event.events.impl.player.EventType
import life.hanabi.event.events.impl.render.EventRender3D
import life.hanabi.utils.math.ColorUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

class DragonWings(name: String) : Module(name, ModuleCategory.Render) {
    var colored = BooleanValue("Colored", "color the wings", false)
    var chroma = BooleanValue("Chroma", "chroma", false)
    var scaled = NumberValue("Scale", "scale", 100.0, 0.0, 100.0, 1.0)
    var color = ColorValue("Color", "color", Color(0, 0, 0).rgb)
    private var renderWings: RenderWings? = null

    init {
        addValues(color, colored, chroma, scaled)
    }

    override fun onEnable() {
        if (renderWings == null) {
            renderWings = RenderWings()
        }
        EventManager.register(renderWings)
    }

    override fun onDisable() {
        EventManager.unregister(renderWings)
    }

    val colors: FloatArray
        get() {
            if (!colored.value) {
                return floatArrayOf(1f, 1f, 1f)
            }
            val color1 = if (chroma.value) Color.getHSBColor(System.currentTimeMillis() % 1000 / 1000f, 0.8f, 1f) else ColorUtils.intToColor(color.color)
            return floatArrayOf(color1.red / 255f, color1.green / 255f, color1.blue / 255f)
        }

    /**
     * @author: https://github.com/Canelex/DragonWingsMod
     */
    class RenderWings : ModelBase() {
        private val location: ResourceLocation = ResourceLocation("client/wings/wings.png")
        private val wing: ModelRenderer
        private val wingTip: ModelRenderer
        private val playerUsesFullHeight: Boolean = false
        private val wingsModule: Module? = ModuleManager.modules["DragonWings"]

        init {
            //Loader.isModLoaded("animations");

            // Set texture offsets.
            setTextureOffset("wing.bone", 0, 0)
            setTextureOffset("wing.skin", -10, 8)
            setTextureOffset("wingtip.bone", 0, 5)
            setTextureOffset("wingtip.skin", -10, 18)

            // Create wing model renderer.
            wing = ModelRenderer(this, "wing")
            wing.setTextureSize(30, 30) // 300px / 10px
            wing.setRotationPoint(-2f, 0f, 0f)
            wing.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2)
            wing.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)

            // Create wing tip model renderer.
            wingTip = ModelRenderer(this, "wingtip")
            wingTip.setTextureSize(30, 30) // 300px / 10px
            wingTip.setRotationPoint(-10.0f, 0.0f, 0.0f)
            wingTip.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1)
            wingTip.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10)
            wing.addChild(wingTip) // Make the wingtip rotate around the wing.
        }

        @EventTarget
        fun onRenderPlayer(event: EventRender3D) {
            if (event.getType() != EventType.POST) {
                return
            }
            if (!mc.thePlayer.isInvisible && mc.gameSettings.thirdPersonView != 0) // Should render wings onto this player?
            {
                renderWings(mc.thePlayer, event.partialTicks)
            }
        }

        private fun renderWings(player: EntityPlayer, partialTicks: Float) {
            val scale: Double = (wingsModule as DragonWings).scaled.value / 100.0
            val rotate = interpolate(player.prevRenderYawOffset, player.renderYawOffset, partialTicks)
            GL11.glPushMatrix()
            GL11.glScaled(-scale, -scale, scale)
            GL11.glRotated(180.0 + rotate, 0.0, 1.0, 0.0) // Rotate the wings to be with the player.
            GL11.glTranslated(0.0, -(if (playerUsesFullHeight) 1.45 else 1.25) / scale, 0.0) // Move wings correct amount up.
            GL11.glTranslated(0.0, 0.0, 0.2 / scale)
            if (player.isSneaking) {
                GL11.glTranslated(0.0, 0.125 / scale, 0.0)
            }
            val colors = wingsModule.colors
            GL11.glColor3f(colors[0], colors[1], colors[2])
            mc.textureManager.bindTexture(location)
            for (j in 0..1) {
                GL11.glEnable(GL11.GL_CULL_FACE)
                val f11 = System.currentTimeMillis() % 1000 / 1000f * Math.PI.toFloat() * 2.0f
                wing.rotateAngleX = Math.toRadians(-80.0).toFloat() - cos(f11.toDouble()).toFloat() * 0.2f
                wing.rotateAngleY = Math.toRadians(20.0).toFloat() + sin(f11.toDouble()).toFloat() * 0.4f
                wing.rotateAngleZ = Math.toRadians(20.0).toFloat()
                wingTip.rotateAngleZ = -(sin((f11 + 2.0f).toDouble()) + 0.5).toFloat() * 0.75f
                wing.render(0.0625f)
                GL11.glScalef(-1.0f, 1.0f, 1.0f)
                if (j == 0) {
                    GL11.glCullFace(1028)
                }
            }
            GL11.glCullFace(1029)
            GL11.glDisable(GL11.GL_CULL_FACE)
            GL11.glColor3f(255f, 255f, 255f)
            GL11.glPopMatrix()
        }

        private fun interpolate(yaw1: Float, yaw2: Float, percent: Float): Float {
            var f = (yaw1 + (yaw2 - yaw1) * percent) % 360
            if (f < 0) {
                f += 360f
            }
            return f
        }
    }
}