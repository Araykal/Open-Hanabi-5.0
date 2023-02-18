package net.minecraft.client.gui;

import life.hanabi.Hanabi;
import life.hanabi.api.MicrosoftLogin;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.gui.font.UFontRenderer;
import life.hanabi.gui.impl.GuiLogin;
import life.hanabi.utils.math.AnimationUtils;
import life.hanabi.utils.math.TimerUtil;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class GuiMainMenu extends GuiScreen {
    private float selectionAnimY;
    private int i1;
    private String cur = "";
    private TimerUtil timer = new TimerUtil();
    private AnimationUtils animation = new AnimationUtils();

    @Override
    public void initGui() {
        super.initGui();
        timer.reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), -1);

        RenderUtil.drawImage(new ResourceLocation("client/logo1.png"), sr.getScaledWidth() / 2f - 100 / 2f, sr.getScaledHeight() / 2f - 110, 100, 37);
        // Language Select
        RenderUtil.drawImage(new ResourceLocation("client/icons/mainmenu/" + (Hanabi.INSTANCE.configManager.getSettings("settings.chinese") ? "english" : "chinese") + ".png"), sr.getScaledWidth() - 40, 10, 24, 24, new Color(200, 200, 200));
        if (RenderUtil.isHoveringAppend(mouseX, mouseY, sr.getScaledWidth() - 40, 10, 24, 24) && Mouse.isButtonDown(0) && timer.hasReached(200)) {
            timer.reset();
            I18NUtils.loadLanguage((Hanabi.INSTANCE.configManager.getSettings("settings.chinese") ? "简体中文" : "English"));
            Hanabi.INSTANCE.configManager.settings.replace("settings.chinese", !Hanabi.INSTANCE.configManager.getSettings("settings.chinese"));
        }
        int x = (sr.getScaledWidth() - 269 / 2) / 2;
        int y = (sr.getScaledHeight() - 200 / 2) / 2;
        int width1 = 269 / 2;
        int height1 = 244 / 2;
        RenderUtil.drawImage(new ResourceLocation("client/guis/mainmenu/selectBG.png"), x, y, width1, height1, new Color(250, 250, 250));

        int height2 = 92 / 2;
        String[] strs = new String[]{"Single Player", "Multi Player", "Settings", "Alts Manager"};
        GlStateManager.disableBlend();
        RenderUtil.drawImage(new ResourceLocation("client/guis/mainmenu/selection.png"), x - 10, selectionAnimY, 309 / 2f, height2);
        if (selectionAnimY == 0) {
            selectionAnimY = y - 10;
            i1 = (int) selectionAnimY;
        }
        selectionAnimY = animation.animate(i1, selectionAnimY, 0.4f);
        for (int i = 0; i < 4; i++) {
            UFontRenderer font = Hanabi.INSTANCE.fontLoaders.syFont18;
            String str = strs[i];
            boolean hoveringAppend = RenderUtil.isHoveringAppend(mouseX, mouseY, x, y, width1, 32);
            if (hoveringAppend || cur.equals(str)) {
                i1 = y - 10;
                cur = str;
                RenderUtil.drawImage(new ResourceLocation("client/icons/mainmenu/" + str.toLowerCase() + ".png"), x + 30, y + 8, 13, 13, new Color(90, 90, 90));
                font.drawString(str, x + 50, y + 10, new Color(90, 90, 90).getRGB());
                GlStateManager.disableBlend();
                if (hoveringAppend && Mouse.isButtonDown(0) && timer.hasReached(200)) {
                    switch (i) {
                        case 0:
                            mc.displayGuiScreen(new GuiSelectWorld(this));
                            break;
                        case 1:
                            mc.displayGuiScreen(new GuiMultiplayer(this));
                            break;
                        case 2:
                            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                            break;
                        case 3:
//                            mc.displayGuiScreen(new GuiLogin(false));
                            MicrosoftLogin.login();
                            break;
                    }
                    timer.reset();
                }
            } else {
                font.drawString(str, x + 50, y + 10, new Color(197, 197, 197).getRGB());
                GlStateManager.disableBlend();
                RenderUtil.drawImage(new ResourceLocation("client/icons/mainmenu/" + str.toLowerCase() + ".png"), x + 30, y + 8, 13, 13, new Color(197, 197, 197));
            }
            y += 32;
        }
        Hanabi.INSTANCE.notificationsManager.draw();

    }
}
