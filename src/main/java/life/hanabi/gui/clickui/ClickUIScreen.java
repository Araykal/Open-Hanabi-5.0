package life.hanabi.gui.clickui;

import life.hanabi.Hanabi;
import life.hanabi.core.Module;
import life.hanabi.core.ModuleCategory;
import life.hanabi.core.managers.ModuleManager;
import life.hanabi.gui.clickui.component.Component;
import life.hanabi.gui.clickui.component.impl.ModuleComponent;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ClickUIScreen extends GuiScreen {
    public static float x, y;
    public static float width = 485, height = 295;
    public static float leftWidth = 110;
    public static float rightWidth = 100;

    public static ArrayList<life.hanabi.gui.clickui.component.Component> components = new ArrayList<>();
    private ModuleCategory currentCategory = ModuleCategory.Combat;
    public static ModuleComponent currentModule;
    private float topHeight = 30;

    private float scrollY = 0;
    private float scrollY2 = 0;


    public Color backgroundColor = new Color(255, 255, 255);
    public Color boxColor = new Color(244, 244, 244);
    public Color themeColor = new Color(24, 143, 254);


    // drag
    float dragX, dragY;
    boolean dragging;
    boolean sizeDragging;


    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(mc);
        if (x <= 0 && y <= 0) {
            x = sr.getScaledWidth() / 2f - width / 2;
            y = sr.getScaledHeight() / 2f - height / 2;
        }
        if (components.isEmpty()) {
            for (Map.Entry<String, Module> module : ModuleManager.modules.entrySet()) {
                components.add(new ModuleComponent(module.getValue()));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        if (sizeDragging) {
            width = mouseX - x + dragX;
            height = mouseY - y + dragY;
        }
        if (!Mouse.isButtonDown(0)) {
            dragging = false;
            sizeDragging = false;
        }
        RenderUtil.drawRoundedRectUsingCircle(x, y, x + width, y + height, 3, backgroundColor.getRGB());
        RenderUtil.drawRoundedRectUsingCircle(x, y, x + leftWidth, y + height, 3, boxColor.getRGB());
        float cy = y + topHeight + 30;
        for (ModuleCategory mc : ModuleCategory.values()) {
            if (RenderUtil.isHovered(x, cy, leftWidth, 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                currentCategory = mc;
            }
            if (currentCategory == mc)
                RenderUtil.drawRect(x, cy, x + leftWidth, cy + 20, themeColor.getRGB());
            RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/" + mc.name().toLowerCase() + ".png"), x + 5, cy + 5, 12, 12, currentCategory == mc ? new Color(255, 255, 255) : new Color(0, 0, 0));
            Hanabi.INSTANCE.fontLoaders.syFont18.drawString(mc.name(), x + 20, cy + 7, currentCategory == mc ? -1 : new Color(0, 0, 0).getRGB());
            cy += 20;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.doGlScissor(x, y + topHeight, width, height - topHeight);
        if (currentModule != null) {
            RenderUtil.drawRoundedRectUsingCircle(x + width - rightWidth, y, x + width, y + height, 3, boxColor.getRGB());
            currentModule.drawSubComponents(x + width - rightWidth + 10, y + topHeight + scrollY2, mouseX, mouseY);
        }
        RenderUtil.drawImage(new ResourceLocation("client/logo1.png"), x + 5, y + 5, 100, 37);
        float modY = y + topHeight + scrollY;
        for (life.hanabi.gui.clickui.component.Component component : components) {
            if (component.module.type != currentCategory) {
                continue;
            }
            component.draw(x + leftWidth + 5, modY, mouseX, mouseY);
            modY += 35;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            if (isHovered(x + leftWidth + 5, y + topHeight, x + width - rightWidth - 10, y + height, mouseX, mouseY))
                scrollY -= 10;
            else if (isHovered(x + width - rightWidth - 10, y + topHeight, x + width, y + height, mouseX, mouseY))
                scrollY2 -= 10;
        } else if (dWheel > 0 && (scrollY + 10) <= 0) {
            if (isHovered(x + leftWidth + 5, y + topHeight, x + width - rightWidth - 10, y + height, mouseX, mouseY))
                scrollY += 10;
            else if (isHovered(x + width - rightWidth - 10, y + topHeight, x + width, y + height, mouseX, mouseY))
                scrollY2 += 10;
        }
    }

    public boolean isHovered(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovered(x, y, x + width, y + topHeight, mouseX, mouseY) && mouseButton == 0) {
            dragX = mouseX - x;
            dragY = mouseY - y;
            dragging = true;
        }

        if (isHovered(x + width - 10, y + height - 10, x + width, y + height, mouseX, mouseY) && mouseButton == 0) {
            dragX = x + width - mouseX;
            dragY = y + height - mouseY;
            sizeDragging = true;
        }

        if (currentModule != null)
            currentModule.mouseClickedSubComponents(mouseX, mouseY, mouseButton);
        for (Component component : components) {
            if (component.module.type != currentCategory) {
                continue;
            }
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
