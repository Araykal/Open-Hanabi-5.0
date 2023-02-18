package life.hanabi.gui.impl;

import life.hanabi.Hanabi;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiEditCustom extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Hanabi.INSTANCE.guiCustom.mouseRelease(mouseX, mouseY);
        Hanabi.INSTANCE.guiCustom.drawScreen(mouseX, mouseY);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Hanabi.INSTANCE.guiCustom.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
