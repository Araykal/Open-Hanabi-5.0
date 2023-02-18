package life.hanabi.gui.util;

import life.hanabi.gui.impl.GuiEditCustom;
import life.hanabi.Hanabi;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.core.Module;
import life.hanabi.utils.math.AnimationUtils;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class DragAble {
    public float x, y, x1, y1, dX, dY;
    public boolean drag;
    public Module mod;
    private Color color = Hanabi.INSTANCE.theme.themeColor;
    AnimationUtils a1 = new AnimationUtils(), a2 = new AnimationUtils(), a3 = new AnimationUtils();

    public DragAble(Module module) {
        this.mod = module;
    }

    public void draw(float mouseX, float mouseY) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        x = mod.x * sr.getScaledWidth();
        x *= mod.getScale();
        y = mod.y * sr.getScaledHeight();
        y *= mod.getScale();
        x1 = x + mod.width;
        x1 *= mod.getScale();
        y1 = y + mod.height;
        y1 *= mod.getScale();

//        GL11.glScalef(mod.getScale(), mod.getScale(), mod.getScale());

//        if (isHovered(x, y - 16, x1, y1, mouseX / mod.getScale(), mouseY / mod.getScale())) {
//            color = new Color((int) a1.animate(32, color.getRed(), 0.25), (int) a2.animate(200, color.getGreen(), 0.25), (int) a3.animate(170, color.getBlue(), 0.25));
//        } else {
//            color = new Color((int) a1.animate(0, color.getRed(), 0.25), (int) a2.animate(0, color.getGreen(), 0.25), (int) a3.animate(0, color.getBlue(), 0.25), (int) a3.animate(0, color.getBlue(), 0.25));
//        }


        mod.onGui();
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
            if (isHovered(x, y - 16, x1, y1, mouseX / mod.getScale(), mouseY / mod.getScale())) {
                Hanabi.INSTANCE.fontLoaders.syFont18.drawStringWithNewShadow(I18NUtils.getString("mod." + mod.name), mouseX + 4f, mouseY + 4f, -1);
            }
        }
//        GlStateManager.scale(1 / mod.getScale(), 1 / mod.getScale(), 0);
    }

    public void mouse(int mouseX, int mouseY) {
        if (!mod.stage && !(Minecraft.getMinecraft().currentScreen instanceof GuiEditCustom)) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        x = mod.x * sr.getScaledWidth();
        y = mod.y * sr.getScaledHeight();
        x1 = x + mod.width;
        y1 = y + mod.height;

        if (!Mouse.isButtonDown(0)) {
            drag = false;
        }
//        }
        if (drag) {
            float w = x1 - x;
            float h = y1 - y;

            int iX = Math.round((int) (x / 50)) * 50;
            x = mouseX + dX - w;
            if (Math.abs(x - iX) < 5) {
                x = iX;
            } else if (Math.abs(x - (iX + 50)) < 5) {
                x = iX + 50;
            }
            mod.setX(x / sr.getScaledWidth());

            int iY = Math.round((int) (y / 50)) * 50;
            y = mouseY + dY - h;
            if (Math.abs(y - iY) < 5) {
                y = iY;
            } else if (Math.abs(y - (iY + 50)) < 5) {
                y = iY + 50;
            }
            mod.setY(y / sr.getScaledHeight());
        }
    }

    public void clicked(int mouseX, int mouseY, int button) {
        if (!mod.stage && !(Minecraft.getMinecraft().currentScreen instanceof GuiEditCustom))
            return;
        if (isHovered(x, y, x1, y1, mouseX / mod.getScale(), mouseY / mod.getScale()) && button == 1) {
            mod.onRight();
        }
        if (isHovered(x, y - 16, x1, y1, mouseX / mod.getScale(), mouseY / mod.getScale())) {
            drag = true;
            dX = x1 - mouseX / mod.getScale();
            dY = y1 - mouseY / mod.getScale();
        }
    }

    public static boolean isHovered(float x, float y, float x2, float y2, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

}
