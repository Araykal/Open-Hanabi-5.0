package life.hanabi.gui.font;

import life.hanabi.Hanabi;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.InputStream;

public class FontLoaders {
    public UFontRenderer arial14;
    public UFontRenderer arial16;
    public UFontRenderer arial18;
    public UFontRenderer arial22;
    public UFontRenderer arial24;
    public UFontRenderer syFont18;
    public UFontRenderer syFont14;
    public UFontRenderer syFont16;
    public UFontRenderer syFont36;
    public UFontRenderer syFont72;
    public UFontRenderer default14, default16, default18, default22, default24;
    public UFontRenderer syFont20;


    public FontLoaders() {
        System.out.println("Started loading fonts");
        long t1 = System.currentTimeMillis();
        arial14 = getArial(14, true);
        arial16 = getArial(16, true);
        arial18 = getArial(18, true);
        arial22 = getArial(22, true);
        arial24 = getArial(24, true);
        syFont14 = getMiSans(14, true);
        syFont16 = getMiSans(16, true);
        syFont18 = getMiSans(18, true);
        syFont20 = getMiSans(20, true);
        syFont36 = getMiSans(36, true);
        syFont72 = getMiSans(72, true);
        default14 = getDefault(14, true);
        default16 = getDefault(16, true);
        default18 = getDefault(18, true);
        default22 = getDefault(22, true);
        default24 = getDefault(24, true);
        System.out.println("Fonts loaded:" + (System.currentTimeMillis() - t1) + "ms");
    }

    public UFontRenderer getDefault(int size, boolean antiAlias) {
        Font font = new Font("default", Font.PLAIN, size);
        return new UFontRenderer(font, size, antiAlias);
    }

    public UFontRenderer getMiSans(int size, boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/misans.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

    private UFontRenderer getClientFont(int size, boolean antiAlias) {
        return getFont("HarmonyOS_Sans_SC_Regular.ttf", size, antiAlias, false);
    }
    public UFontRenderer getFont(String fontName, int size, boolean antiAlias, boolean bold) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/" + fontName)).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(bold ? Font.BOLD : Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            font = new Font("default", bold ? Font.BOLD : Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }
    public UFontRenderer getArial(int size, boolean antiAlias) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("client/fonts/arial.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new UFontRenderer(font, size, antiAlias);
    }

}

