package life.hanabi.core.managers;

import life.hanabi.Hanabi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.HashMap;

public class FontManager {
    private static final HashMap<Integer, FontRenderer> fonts = new HashMap<>();
    private static final ArrayList<String> nameList = new ArrayList<>();
    private static int index = 0;
    private static String current = "Original";

    public static void init() {
        fonts.put(0, Minecraft.getMinecraft().fontRendererObj);
        nameList.add("Original");
        fonts.put(1, Hanabi.INSTANCE.fontLoaders.syFont18);
        nameList.add("Smooth");
    }

    public static FontRenderer getCurrentFont() {
        return fonts.get(index);
    }

    public static void setFont() {
        ++index;
        if (index >= fonts.size()) {
            index = 0;
        }
        current = nameList.get(index);
    }

    public static String getCurrent() {
        return current;
    }
}
