package life.hanabi.gui.impl;

import life.hanabi.gui.keystrokes.KeyStrokes;
import life.hanabi.gui.keystrokes.render.KeystrokesRenderer;
import life.hanabi.gui.util.DragAble;
import life.hanabi.Hanabi;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;

public class GuiCustom {
    public KeyStrokes keyStrokes = new KeyStrokes();
    public static ArrayList<DragAble> dragAbles = new ArrayList<>();

    public GuiCustom() {
        keyStrokes.setRenderer(new KeystrokesRenderer(keyStrokes));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("Sprint")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("PotionDisplay")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("MemoryManager")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("KeyStrokes")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("Scoreboard")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("Coordinates")));
        dragAbles.add(new DragAble(Hanabi.INSTANCE.moduleManager.modules.get("ArmorStatus")));
    }

    public void drawScreen(int mouseX, int mouseY) {
        for (DragAble module : dragAbles) {
            if (module.mod.stage) {
                module.draw(mouseX, mouseY);
            }
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
    }

    public void mouseRelease(int mouseX, int mouseY) {
        for (DragAble module : dragAbles) {
            if (module.mod.stage) {
                module.mouse(mouseX, mouseY);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (DragAble module : dragAbles) {
            module.clicked(mouseX, mouseY, mouseButton);
        }

    }

}
