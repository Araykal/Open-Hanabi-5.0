package life.hanabi.gui.impl;

import net.minecraft.client.gui.*;
import life.hanabi.Hanabi;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.core.managers.FontManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

import java.awt.*;
import java.io.IOException;

/**
 * @author QianXia
 **/
public class GuiSettings extends GuiScreen {
    private final GuiScreen parent;

    public GuiSettings(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        //获取settings请勿使用configManager.getSettings() ，应使用 getSettings()方法
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 6 - 6, 150, 20, I18NUtils.getString("settings.ExpandOnHover") + (Hanabi.INSTANCE.configManager.getSettings("settings.ExpandOnHover") ? ":On" : ":Off")));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 155, this.height / 6 + 48 + 24 - 6, 150, 20, I18NUtils.getString("settings.TabNoPlayerName") + (Hanabi.INSTANCE.configManager.getSettings("settings.TabNoPlayerName") ? ":On" : ":Off")));
        this.buttonList.add(new GuiButton(7, this.width / 2 + 5, this.height / 6 + 48 + 24 - 6, 150, 20, I18NUtils.getString("settings.NoChatShow") + (Hanabi.INSTANCE.configManager.getSettings("settings.NoChatShow") ? ":On" : ":Off")));
        this.buttonList.add(new GuiButton(99, this.width / 2 - 155, this.height / 6 + 72 + 24 - 6, 150, 20, I18NUtils.getString("settings.Font") + ": " + FontManager.getCurrent()));
        this.buttonList.add(new GuiOptionButton(100, this.width / 2 + 5, this.height / 6 + 72 + 24 - 6, GameSettings.Options.FORCE_UNICODE_FONT, mc.gameSettings.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)));

        this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawString(mc.fontRendererObj, I18NUtils.getString("settings.client"), this.width / 2 - fontRendererObj.getStringWidth(I18NUtils.getString("settings.client")) / 2, 15, new Color(255, 255, 255).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                Hanabi.INSTANCE.configManager.settings.replace("settings.ExpandOnHover", !Hanabi.INSTANCE.configManager.getSettings("settings.ExpandOnHover"));
                button.displayString = I18NUtils.getString("settings.ExpandOnHover") + (Hanabi.INSTANCE.configManager.getSettings("settings.ExpandOnHover") ? ":On" : ":Off");
                break;
            case 2:
                Hanabi.INSTANCE.configManager.settings.replace("settings.ChatBackground", !Hanabi.INSTANCE.configManager.getSettings("settings.ChatBackground"));
                button.displayString = I18NUtils.getString("settings.ChatBackground") + (Hanabi.INSTANCE.configManager.getSettings("settings.ChatBackground") ? ":On" : ":Off");
                break;
            case 3:
                Hanabi.INSTANCE.configManager.settings.replace("settings.ChatBar", !Hanabi.INSTANCE.configManager.getSettings("settings.ChatBar"));
                button.displayString = I18NUtils.getString("settings.ChatBar") + (Hanabi.INSTANCE.configManager.getSettings("settings.ChatBar") ? ":On" : ":Off");
                break;
            case 6:
                Hanabi.INSTANCE.configManager.settings.replace("settings.TabNoPlayerName", !Hanabi.INSTANCE.configManager.getSettings("settings.TabNoPlayerName"));
                button.displayString = I18NUtils.getString("settings.TabNoPlayerName") + (Hanabi.INSTANCE.configManager.getSettings("settings.TabNoPlayerName") ? ":On" : ":Off");
                break;
            case 7:
                Hanabi.INSTANCE.configManager.settings.replace("settings.NoChatShow", !Hanabi.INSTANCE.configManager.getSettings("settings.NoChatShow"));
                button.displayString = I18NUtils.getString("settings.NoChatShow") + (Hanabi.INSTANCE.configManager.getSettings("settings.NoChatShow") ? ":On" : ":Off");
                break;
            case 99:
                FontManager.setFont();
                button.displayString = I18NUtils.getString("settings.Font") + ": " + FontManager.getCurrent();
                mc.fontRendererObj = FontManager.getCurrentFont();
                if(FontManager.getCurrent().equals("Original")){
                    mc.fontRendererObjWithoutUnicode = mc.backupFont;
                }else {
                    mc.fontRendererObjWithoutUnicode = FontManager.getCurrentFont();
                }
                break;
            case 100:
                if (button instanceof GuiOptionButton) {
                    mc.gameSettings.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
                    button.displayString = mc.gameSettings.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
                    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                    int i = scaledresolution.getScaledWidth();
                    int j = scaledresolution.getScaledHeight();
                    this.setWorldAndResolution(this.mc, i, j);
                }
                break;
            case -1:
                mc.displayGuiScreen(parent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }
}