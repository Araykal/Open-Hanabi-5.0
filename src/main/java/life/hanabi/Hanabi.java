package life.hanabi;

import cn.qiriyou.IIiIIiiiIiii;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import life.hanabi.config.ConfigManager;
import life.hanabi.config.theme.LightTheme;
import life.hanabi.core.managers.CommandManager;
import life.hanabi.gui.font.FontLoaders;
import life.hanabi.gui.impl.GuiCustom;
import life.hanabi.gui.notification.NotificationsManager;
import life.hanabi.core.managers.ModuleManager;
import life.hanabi.irc.IRCClient;
import life.hanabi.config.theme.Theme;
import net.minecraft.client.Minecraft;
import life.hanabi.config.Language;
import org.jetbrains.annotations.NotNull;

/**
 * @author SuperSkidder
 */
@IIiIIiiiIiii
public class Hanabi {

    public static Hanabi INSTANCE = new Hanabi();
    public static final String CLIENT_NAME = "Hanabi";
    public static final String VERSION = "5.0";

    public final String DESCRIPTION = " closed preview";
    public FontLoaders fontLoaders;
    public GuiCustom guiCustom;
    public Language language;
    public ModuleManager moduleManager;
    public ConfigManager configManager;
    public CommandManager commandManager;
    public Theme theme = new LightTheme();
    public Minecraft mc;
    public NotificationsManager notificationsManager;
    @NotNull
    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public boolean loggedIn;
    public IRCClient client;
    public String rank;

    public void initClient() {
        fontLoaders = new FontLoaders();
        Hanabi.INSTANCE.moduleManager = new ModuleManager();
        Hanabi.INSTANCE.moduleManager.init();
        Hanabi.INSTANCE.language = new Language();
        guiCustom = new GuiCustom();
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        commandManager.init();
        client = new IRCClient();
        client.connect();
        mc = Minecraft.getMinecraft();
        notificationsManager = new NotificationsManager();
    }

    public void println(String code) {
        System.out.println(code);
    }
}
