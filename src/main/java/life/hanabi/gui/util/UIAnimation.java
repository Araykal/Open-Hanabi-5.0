package life.hanabi.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class UIAnimation {
    public static void sizeAnimate(float x, float y, float width, float height, float progress) {
        GL11.glScaled(progress, progress, 1);
        float xpos = (x + width / 2 / progress) * (1 - progress);
        float ypos = (y + height / 2 / progress) * (1 - progress);
        GL11.glTranslated(xpos, ypos, 0);
    }
}
