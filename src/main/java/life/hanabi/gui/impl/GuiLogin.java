package life.hanabi.gui.impl;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;
import life.hanabi.gui.util.RoundButton;
import life.hanabi.gui.util.RoundInputBox;
import life.hanabi.irc.ClientHandler;
import life.hanabi.irc.packets.impl.clientside.PacketLogin;
import life.hanabi.irc.utils.Check;
import life.hanabi.irc.utils.PacketUtil;
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
import life.hanabi.Hanabi;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.utils.math.AnimationUtils;
import life.hanabi.utils.math.ColorUtils;
import life.hanabi.utils.render.RenderUtil;
import life.hanabi.utils.math.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;

/**
 * @author Super
 */
public class GuiLogin extends GuiScreen {
    TimerUtil timerUtil = new TimerUtil();
    AnimationUtils animationUtils = new AnimationUtils();
    int rainbowTick = 0;
    float alpha = 10f;
    RoundInputBox username;
    RoundInputBox password;
    RoundButton cancel;
    RoundButton login;
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    String staus = "Idle...";

    public GuiLogin() {
    }

    @Override
    public void initGui() {
        super.initGui();
        sr = new ScaledResolution(mc);
        String temp1 = username == null ? "" : username.getText();
        String temp2 = password == null ? "" : password.getText();

        username = new RoundInputBox(1, Hanabi.INSTANCE.fontLoaders.syFont18, sr.getScaledWidth() / 2 - 80, sr.getScaledHeight() / 3, 160, 20, false);
        password = new RoundInputBox(1, Hanabi.INSTANCE.fontLoaders.syFont18, sr.getScaledWidth() / 2 - 80, sr.getScaledHeight() / 3 + 40, 160, 20, true);
        username.setText(temp1);
        password.setText(temp2);
        cancel = new RoundButton(I18NUtils.getString("gui.login.cancel"), sr.getScaledWidth() / 2f - 80, sr.getScaledHeight() / 3f + 80, 70, 21.5f, Hanabi.INSTANCE.theme.themeColor, Hanabi.INSTANCE.theme.language_bg, Color.WHITE, Color.BLACK, () -> {
            mc.displayGuiScreen(new GuiMainMenu());
        }, false);

        login = new RoundButton(I18NUtils.getString("gui.login.done"), sr.getScaledWidth() / 2f + 10, sr.getScaledHeight() / 3f + 80, 70, 21.5f, Hanabi.INSTANCE.theme.themeColor, Hanabi.INSTANCE.theme.language_bg, Color.WHITE, Color.BLACK, () -> {
            if (password.getText().equals("")) {
                if (username.getText().equals("")) {
                    staus = ChatFormatting.RED + "Username cannot be empty!.";
                    return;
                }
            }
            ClientHandler.context.writeAndFlush(PacketUtil.pack(new PacketLogin(username.getText(), password.getText(), Check.getHWID(), Hanabi.VERSION)));

        }, true);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        alpha = animationUtils.animate(0, alpha, 0.1f, true);
        int c4 = new Color(0, 0, 0).getRGB();
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(235, 235, 235).getRGB());

        if (timerUtil.delay(50)) {
            if (rainbowTick++ > 50) {
                rainbowTick = 0;
            }
            timerUtil.reset();
        }


        Hanabi.INSTANCE.fontLoaders.arial24.drawCenteredString(Hanabi.CLIENT_NAME, sr.getScaledWidth() / 2f, sr.getScaledHeight() / 3f - 30, new Color(0, 0, 0).getRGB());
        Hanabi.INSTANCE.fontLoaders.arial16.drawCenteredString(staus, sr.getScaledWidth() / 2f, sr.getScaledHeight() / 3f - 15, new Color(50, 50, 50).getRGB());

        username.drawTextBox();
        password.drawTextBox();
        login.mouseReleased(mouseX, mouseY);
        cancel.mouseReleased(mouseX, mouseY);
        cancel.drawButton();
        login.drawButton();
        RenderUtil.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), ColorUtils.reAlpha(c4, alpha / 10f));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        login.mouseClicked(mouseX, mouseY, mouseButton);
        cancel.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        username.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

}