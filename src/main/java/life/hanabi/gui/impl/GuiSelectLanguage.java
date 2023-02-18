package life.hanabi.gui.impl;

import life.hanabi.config.ConfigManager;
import life.hanabi.gui.util.RoundButton;
import life.hanabi.Hanabi;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class GuiSelectLanguage extends GuiScreen {
    RoundButton btn_done;
    private String selected;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), Hanabi.INSTANCE.theme.language_bg.getRGB());
        RenderUtil.drawImageShadow(sr.getScaledWidth() / 2f - 100, (sr.getScaledHeight() - ConfigManager.Companion.getSupported().size() * 20) / 2f, sr.getScaledWidth() / 2f + 100, (sr.getScaledHeight() + ConfigManager.Companion.getSupported().size() * 20) / 2f, 6);
        RenderUtil.drawRoundedRectUsingCircle(sr.getScaledWidth() / 2f - 100, (sr.getScaledHeight() - ConfigManager.Companion.getSupported().size() * 20) / 2f, sr.getScaledWidth() / 2f + 100, (sr.getScaledHeight() + ConfigManager.Companion.getSupported().size() * 20) / 2f, 2, Hanabi.INSTANCE.theme.language_bg.getRGB());
        Hanabi.INSTANCE.fontLoaders.syFont18.drawString("Select Language", sr.getScaledWidth() / 2f - 100, (sr.getScaledHeight() - ConfigManager.Companion.getSupported().size() * 20) / 2 - 15, Color.BLACK.getRGB());

        float lY = (sr.getScaledHeight() - ConfigManager.Companion.getSupported().size() * 20) / 2f + 5;
        for (String s : ConfigManager.Companion.getSupported()) {
            if (s.equals(selected)) {
                RenderUtil.drawRoundedRectUsingCircle(sr.getScaledWidth() / 2f - 100, lY - 5, sr.getScaledWidth() / 2f + 100, lY + 15, 2, Hanabi.INSTANCE.theme.themeColor.getRGB());
                Hanabi.INSTANCE.fontLoaders.syFont18.drawCenteredString(s, sr.getScaledWidth() / 2f, lY, Hanabi.INSTANCE.theme.language_text_sel.getRGB());
            } else {
                if (isHovered(sr.getScaledWidth() / 2f - 100, lY - 5, sr.getScaledWidth() + 100, lY + 14, mouseX, mouseY)) {
                    RenderUtil.drawRoundedRectUsingCircle(sr.getScaledWidth() / 2f - 100, lY - 5, sr.getScaledWidth() / 2f + 100, lY + 15, 2, Hanabi.INSTANCE.theme.language_sel.getRGB());
                }
                Hanabi.INSTANCE.fontLoaders.syFont18.drawCenteredString(s, sr.getScaledWidth() / 2f, lY, Hanabi.INSTANCE.theme.language_text_unsel.getRGB());
            }
            lY += 20;
        }

        btn_done.drawButton();
        btn_done.mouseReleased(mouseX, mouseY);

    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        btn_done.mouseClicked(mouseX, mouseY, mouseButton);
        float lY = (sr.getScaledHeight() - ConfigManager.Companion.getSupported().size() * 20) / 2.0F + 5;
        for (String s : ConfigManager.Companion.getSupported()) {
            if (isHovered(sr.getScaledWidth() / 2.0F - 100, lY - 5, sr.getScaledWidth() + 100, lY + 14, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                I18NUtils.loadLanguage(s);
                selected = s;
                btn_done.string = I18NUtils.getString("language.done");
            }
            lY += 20;
        }

    }
    ScaledResolution sr;
    @Override
    public void initGui() {
        super.initGui();
        Hanabi.INSTANCE.configManager.reloadLanguages();
        selected = ConfigManager.Companion.getLanguage();
        sr = new ScaledResolution(mc);
        I18NUtils.loadLanguage(selected);
        btn_done = new RoundButton(I18NUtils.getString("language.done"), sr.getScaledWidth() / 2f + 10, (sr.getScaledHeight() + ConfigManager.Companion.getSupported().size() * 20) / 2f + 10, 90, 20, Hanabi.INSTANCE.theme.themeColor, Hanabi.INSTANCE.theme.language_bg, Color.WHITE, Color.BLACK, () -> {
            ConfigManager.Companion.setLanguage(this.selected);
            mc.displayGuiScreen(new GuiMainMenu());
        }, false);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }


}
