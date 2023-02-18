package life.hanabi.gui.customgui;

import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;

public class GuiInput extends GuiScreen {
    GuiTextField field;
    Runnable run;
    public static String s = "";

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(mc);
        field = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 50, sr.getScaledHeight() / 2 - 10, 100, 20);
        field.setMaxStringLength(Integer.MAX_VALUE);
        field.setText(s);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 200).getRGB());
        field.drawTextBox();
        mc.fontRendererObj.drawStringWithShadow("We will trans '&' to color, like '&4[Test]' will be shown as '\2474[Test]\247f'.", sr.getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth("We will trans '&' to color, like '&4[Test]' will be shown as '[Test]'.") / 2f, sr.getScaledHeight() / 2f + 20, -1);
        mc.fontRendererObj.drawStringWithShadow("Input '&&' to show a '&'.", sr.getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth("Input '&&' to show a '&'.") / 2f, sr.getScaledHeight() / 2f + 32, -1);
        mc.fontRendererObj.drawStringWithShadow("When you finished inputting, just press \2474[ESC]\247f.", sr.getScaledWidth() / 2f - mc.fontRendererObj.getStringWidth("When you finished inputting, just press [ESC]") / 2f, sr.getScaledHeight() / 2f + 44, -1);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        field.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        field.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        s = field.getText();
        if (run != null) {
            run.run();
        }
    }
}
