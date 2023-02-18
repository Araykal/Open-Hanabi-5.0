package life.hanabi.gui.keystrokes.keys.impl;

import life.hanabi.Hanabi;
import life.hanabi.gui.keystrokes.KeyStrokes;
import life.hanabi.gui.keystrokes.keys.AbstractKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class FPSKey extends AbstractKey {
    public FPSKey(KeyStrokes mod, int xOffset, int yOffset) {
        super(mod, xOffset, yOffset);
    }

    public void renderKey(int x, int y) {
        int yOffset = this.yOffset;
        if (((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showCPSOnButtons.getValue() || !((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showCPS.getValue()) {
            yOffset -= 18;
        }

        if (!((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showSpacebar.getValue()) {
            yOffset -= 18;
        }

        if (!((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showSneak.getValue()) {
            yOffset -= 18;
        }

        if (!((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showMouseButtons.getValue()) {
            yOffset -= 24;
        }

        if (!((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).showWASD.getValue()) {
            yOffset -= 48;
        }

        int textColor = this.getColor();
        if (((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).keyBackground.getValue()) {
            Gui.drawRect(x + this.xOffset, y + yOffset, x + this.xOffset + 70, y + yOffset + 16,((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).backgroundColor.getColor());
        }

        String name = Minecraft.getDebugFPS() + " FPS";
        if (((life.hanabi.modules.render.KeyStrokes) Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")).chroma.getValue()) {
            this.drawChromaString(name, x + (this.xOffset + 70) / 2 - this.mc.fontRendererObjWithoutUnicode.getStringWidth(name) / 2, y + yOffset + 4, 1.0D);
        } else {
            this.drawCenteredString(name, x + (this.xOffset + 70) / 2, y + yOffset + 4, textColor);
        }

    }
}
