package life.hanabi.gui.clickgui;

import life.hanabi.Hanabi;
import life.hanabi.core.managers.ModuleManager;
import life.hanabi.core.values.values.ModeValue;
import life.hanabi.core.values.values.ColorValue;
import life.hanabi.gui.util.UIAnimation;
import life.hanabi.core.I18N.I18NUtils;
import life.hanabi.core.Module;
import life.hanabi.core.ModuleCategory;
import life.hanabi.core.values.Value;
import life.hanabi.core.values.values.BooleanValue;
import life.hanabi.core.values.values.NumberValue;
import life.hanabi.utils.NotificationType;
import life.hanabi.utils.NotificationsUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import life.hanabi.utils.math.AnimationUtils;
import life.hanabi.utils.render.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ClickGui extends GuiScreen {
    private static float x = -1, y = -1, width = 0, height = 0;
    private boolean drag;
    private float dragX, dragY;
    private static Module curModule;
    private static ModuleCategory curType = ModuleCategory.Combat;
    private final boolean doesGuiPauseGame;
    private final float leftWidth = 90;
    private static final Slider slider = new Slider();
    private boolean waitingToBind;
    private static float modsWheel;
    private Module boundModule;
    private boolean sizeDrag = false;
    private float sizeDragX;
    private float sizeDragY;
    private final AnimationUtils guiOpenAnimation = new AnimationUtils();
    private float openAnimation = 60;
    private final AnimationUtils mouseScrollAnimation = new AnimationUtils();
    private static float wheel_temp;
    private final AnimationUtils listAnimation = new AnimationUtils();
    private float listAnim = 60;
    private float valuesWheel;
    private final AnimationUtils listMouseScrollAnimation = new AnimationUtils();
    private int wheel_temp1;

    public ClickGui(boolean doesGuiPauseGame) {
        this.doesGuiPauseGame = doesGuiPauseGame;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return this.doesGuiPauseGame;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        slider.update();
        ScaledResolution sr = new ScaledResolution(mc);
        openAnimation = guiOpenAnimation.animate(100, openAnimation, 0.35f, false);
        if (!Mouse.isButtonDown(0)) {
            drag = false;
            sizeDrag = false;
        }
        if (drag && !sizeDrag) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        float w = mouseX + sizeDragX - x;
        float h = mouseY + sizeDragY - y;
        if (sizeDrag && (w > 400) && (w < 700)) {
            width = w;
        }
        if (sizeDrag && (h > 310) && (h < 400)) {
            height = h;
        }
        UIAnimation.sizeAnimate(x, y, width, height, openAnimation / 100);

        //绘制主窗体
        RenderUtil.drawRoundedRectUsingCircle(x - 1, y - 1, x + width + 1, y + height + 1, 2, Hanabi.INSTANCE.theme.outline.getRGB());
        RenderUtil.drawRoundedRectUsingCircle(x, y, x + width, y + height, 2, Hanabi.INSTANCE.theme.bg.getRGB());
        RenderUtil.drawRoundedRectUsingCircle(x, y, x + leftWidth, y + height, 2, Hanabi.INSTANCE.theme.left.getRGB());

        RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/logo.png"), x + 5, y + 25, 174 / 2f, 43 / 2f);
        RenderUtil.drawImage(new ResourceLocation("client/guis/clickgui/drag.png"), x + width - 20, y + height - 20, 16, 16, Hanabi.INSTANCE.theme.drag);
        RenderUtil.drawRect(x + leftWidth, y, x + leftWidth + 0.5, y + height, Hanabi.INSTANCE.theme.valueSpiltLine);
        //绘制categories
        float my = y + 80;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (curType == m) {
                RenderUtil.drawRoundedRectUsingCircle(x, y + slider.top - 4, x + leftWidth, y + slider.bottom + 4, 1, Hanabi.INSTANCE.theme.themeColor.getRGB());
                RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/" + m.name().toLowerCase(Locale.ROOT) + ".png"), x + 8, my - 2, 12, 12, Hanabi.INSTANCE.theme.curType);
            } else {
                RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/" + m.name().toLowerCase(Locale.ROOT) + ".png"), x + 8, my - 2, 12, 12, Hanabi.INSTANCE.theme.type);
            }
            Hanabi.INSTANCE.fontLoaders.syFont18.drawString(I18NUtils.getString("type." + m.name()), x + 28, my, curType == m ? Hanabi.INSTANCE.theme.curType.getRGB() : Hanabi.INSTANCE.theme.type.getRGB());
            my += 30;
        }

        //绘制功能列表
        if (openAnimation == 100) {
            listAnim = listAnimation.animate(100, listAnim, 0.25f, false);
            UIAnimation.sizeAnimate(x + leftWidth, y, width - leftWidth, height, listAnim / 100);
        } else {
            listAnim = 100;
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        float modsY = y + 10 + modsWheel;
        float valuesY = y + 40 + valuesWheel;

        RenderUtil.doGlScissor(x, y + 10, width, height - 40);
        if (curModule != null) {
            Hanabi.INSTANCE.fontLoaders.syFont18.drawString(I18NUtils.getString("mod." + curModule.name), x + leftWidth + 25, y + 14, Hanabi.INSTANCE.theme.sec_sel.getRGB());
            RenderUtil.drawRect(x + leftWidth + 10, y + 26, x + width, y + 27, Hanabi.INSTANCE.theme.valueSpiltLine);
//            RenderUtil.doGlScissor(x, y + 40, width, height - 40);
            for (Value v : curModule.getValues()) {
                float vw1 = Hanabi.INSTANCE.fontLoaders.syFont18.drawString(I18NUtils.getString("mod." + curModule.name + "." + v.name), x + leftWidth + 26, valuesY, Hanabi.INSTANCE.theme.valueTitle.getRGB());
                if (v instanceof BooleanValue) {
                    RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/disabled.png"), x + leftWidth + 16, valuesY, 8, 8, new Color(255, 255, 255, ((int) (255 - v.animation))));
                    RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/enabled.png"), x + leftWidth + 16, valuesY, 8, 8, new Color(255, 255, 255, ((int) v.animation)));
                    v.animation = v.animationutil.animate(((boolean) v.getValue()) ? 255 : 0, v.animation, 0.2f);
                } else if (v instanceof NumberValue) {
                    RenderUtil.drawRoundedRect2(x + width - 110, valuesY + 1, x + width - 30, valuesY + 9, Hanabi.INSTANCE.theme.option_bg.getRGB());
                    float vX = (((Number) v.getValue()).floatValue() - ((NumberValue<?>) v).getMin().floatValue()) / (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue());
                    v.animation = v.animationutil.animate(vX * 70, v.animation, 0.2f);
                    RenderUtil.drawRoundedRect2(x + width - 110, valuesY + 1, x + width - 100 + v.animation, valuesY + 9, Hanabi.INSTANCE.theme.themeColor.getRGB());
                    Hanabi.INSTANCE.fontLoaders.arial14.drawString(((int) (((Number) v.getValue()).doubleValue() * 10)) / 10f + "", x + width + v.animation - 105 - Hanabi.INSTANCE.fontLoaders.arial14.getStringWidth(((int) (((Number) v.getValue()).doubleValue() * 10)) / 10f + ""), valuesY, -1, false);
                    if (((NumberValue<?>) v).drag && Mouse.isButtonDown(0) && valuesY > y && valuesY + 20 < y + height) {
                        float v1 = (mouseX - (x + width - 100)) / 70 * (((NumberValue<?>) v).getMax().floatValue() - ((NumberValue<?>) v).getMin().floatValue()) + ((NumberValue<?>) v).getMin().floatValue();
                        if (Math.abs(v1 - ((Number) v.getValue()).floatValue()) >= ((NumberValue<?>) v).getInc().floatValue()) {
                            v.setValue(((Number) v.getValue()).floatValue() + (v1 > ((Number) v.getValue()).floatValue() ? ((NumberValue<?>) v).getInc().floatValue() : -((NumberValue<?>) v).getInc().floatValue()));
                        }
                        if (v1 <= ((NumberValue<?>) v).getMin().floatValue()) {
                            v.setValue(((NumberValue<?>) v).getMin().floatValue());
                        }
                        if (v1 >= ((NumberValue<?>) v).getMax().floatValue()) {
                            v.setValue(((NumberValue<?>) v).getMax().floatValue());
                        }
                    } else {
                        ((NumberValue<?>) v).drag = false;
                    }
                } else if (v instanceof ModeValue) {
                    RenderUtil.drawRoundedRectUsingCircle(x + width - 110, valuesY + 1, x + width - 30, valuesY + 15 + v.animation, 2, Hanabi.INSTANCE.theme.option_bg.getRGB());
                    Hanabi.INSTANCE.fontLoaders.arial18.drawString(((ModeValue) v).getValue(), x + width - 100, valuesY + 5, new Color(50, 50, 50).getRGB(), false);
                    v.animation = v.animationutil.animate((((ModeValue) v).isExpanded() ? ((((ModeValue) v).getModes().length - 1) * 15) : 0), v.animation, 0.14f);
                    float v1 = v.animation / (((ModeValue) v).isExpanded() ? ((((ModeValue) v).getModes().length - 1) * 15) : 0);
                    if (((ModeValue) v).isExpanded()) {
                        for (String mode : ((ModeValue) v).getModes()) {
                            if (mode.equals(v.getValue()))
                                continue;
                            Hanabi.INSTANCE.fontLoaders.arial18.drawString(mode, x + width - 100, valuesY + 15 + 5 / 2f, new Color(180, 180, 180, Math.min(255, Math.max((int)(v1 * 255), 0))).getRGB(), false);
                            if (isHovered(x + width - 110, valuesY + 15, x + width - 30, valuesY + 30, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                v.setValue(mode);
                                ((ModeValue) v).setExpanded(false);
                            }
                            valuesY += 15;
                        }
                    }

                    valuesY += 5;
                } else if (v instanceof ColorValue) {
                    ((ColorValue) v).draw(x + width - 90, valuesY + 1, 40, 40, mouseX, mouseY);
                    valuesY += 30;
                }
                valuesY += 20;
                RenderUtil.drawRect(x + leftWidth + 10, valuesY - 6, x + width - 10, valuesY - 5.5, Hanabi.INSTANCE.theme.valueSpiltLine);
            }
        } else {
            for (Map.Entry<String, Module> m : ModuleManager.modules.entrySet()) {
                if (m.getValue().type == curType) {
                    if (modsY < (y + height - 20)) {
                        int sc1 = m.getValue().stage ? Hanabi.INSTANCE.theme.sec_sel.getRGB() : Hanabi.INSTANCE.theme.sec_unsel.getRGB();
                        int sc2 = m.getValue().stage ? Hanabi.INSTANCE.theme.desc_sel.getRGB() : Hanabi.INSTANCE.theme.desc_unsel.getRGB();
                        int rc2 = m.getValue().stage ? Hanabi.INSTANCE.theme.option_on.getRGB() : Hanabi.INSTANCE.theme.option_off.getRGB();
                        if (m.getValue() == curModule) {
                            RenderUtil.drawRoundedRectUsingCircle(x + leftWidth + 8, modsY + 20, x + width - 8, modsY + 30 + m.getValue().valueAnimationY, 3, Hanabi.INSTANCE.theme.modbox_down.getRGB());
                        }
                        RenderUtil.drawRoundedRectUsingCircle(x + leftWidth + 8, modsY, x + width - 8, modsY + 30, 3, Hanabi.INSTANCE.theme.modbox.getRGB());
                        float w1 = Hanabi.INSTANCE.fontLoaders.syFont18.drawString(" " + I18NUtils.getString("mod." + m.getValue().name), x + leftWidth + 12, modsY + 15 - Hanabi.INSTANCE.fontLoaders.arial16.getHeight() / 2f, sc1);
                        m.getValue().optionAnimationX = m.getValue().optionAnimationUtils.animate(m.getValue().stage ? 10 : 0, m.getValue().optionAnimationX, 0.2f, false);
                        if (m.getValue().canBeEnabled) {
                            RenderUtil.drawRoundedRectUsingCircle(x + width - 50, modsY + 10, x + width - 30, modsY + 20, 2, Hanabi.INSTANCE.theme.option_bg.getRGB());
                            RenderUtil.circle(x + width - 45 + m.getValue().optionAnimationX, modsY + 15, 3, rc2);
                        }
                    }
                    modsY += 35;
                }
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //mods列表滑动
        if (curModule == null) {
            float mouseDWheel = Mouse.getDWheel();
            if (mouseDWheel > 0 && wheel_temp < 0) {
                wheel_temp += 16;
            } else if (mouseDWheel < 0 && modsY > y + height - 20) {
                wheel_temp -= 16;
            }
            modsWheel = mouseScrollAnimation.animate(wheel_temp, modsWheel, 0.2f);
        } else {
            float mouseDWheel = Mouse.getDWheel();
            if (mouseDWheel > 0 && wheel_temp1 < 0) {
                wheel_temp1 += 16;
            } else if (mouseDWheel < 0 && valuesY > y + height - 20) {
                wheel_temp1 -= 16;
            }
            valuesWheel = listMouseScrollAnimation.animate(wheel_temp1, valuesWheel, 0.2f);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(mc);
        if (width == 0 || height == 0) {
            width = 450;
            height = 380;
        }
        if (x <= 1 || y <= 1) {
            x = (sr.getScaledWidth() - width) / 2;
            y = (sr.getScaledHeight() - height) / 2;
        }
        if (slider.top == 0) {
            slider.change(y + 80 - 4 - y, y + 80 + Hanabi.INSTANCE.fontLoaders.syFont18.getHeight() + 4 - y);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Hanabi.INSTANCE.configManager.saveConfig("current");

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (waitingToBind) {
            if (boundModule == null) {
                NotificationsUtils.sendMessage(NotificationType.ERROR, "Bind module is null");
                waitingToBind = false;
                return;
            }
            boundModule.setKey(keyCode);
            if (keyCode == Keyboard.KEY_ESCAPE) {
                boundModule.setKey(0);
                if (boundModule.name.equalsIgnoreCase("ClickGui")) {
                    boundModule.setKey(Keyboard.KEY_RSHIFT);
                    NotificationsUtils.sendMessage(NotificationType.INFO, boundModule.name + " has been bound to RSHIFT");
                } else {
                    NotificationsUtils.sendMessage(NotificationType.INFO, boundModule.name + " has been bound to " + Keyboard.getKeyName(0));
                }
            } else {
                NotificationsUtils.sendMessage(NotificationType.INFO, boundModule.name + " has been bound to " + Keyboard.getKeyName(keyCode));
            }
            waitingToBind = false;
        } else {
            if (keyCode == 1) {
                this.mc.displayGuiScreen(null);

                if (this.mc.currentScreen == null) {
                    this.mc.setIngameFocus();
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && isHovered(x, y, x + leftWidth, y + 34, mouseX, mouseY)) {
            drag = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }
        if (mouseButton == 0 && isHovered(x + width - 20, y + height - 20, x + width - 4, y + height - 4, mouseX, mouseY)) {
            sizeDrag = true;
            sizeDragX = (x + width) - mouseX;
            sizeDragY = (y + height) - mouseY;
        }

        float my = y + 80;
        for (ModuleCategory m : ModuleCategory.values()) {
            if (isHovered(x + 0, my - 5, x + 79, my + Hanabi.INSTANCE.fontLoaders.syFont18.getHeight() + 5, mouseX, mouseY)) {
                slider.change(my - 4 - y, my + Hanabi.INSTANCE.fontLoaders.syFont18.getHeight() + 4 - y);
                wheel_temp = 0;
                modsWheel = 0;
                curType = m;
                if (curType == ModuleCategory.Settings) {
                    curModule = ModuleManager.modules.get("ClientSettings");
                } else {
                    curModule = null;
                }
                listAnim = 60;
            }
            my += 30;
        }

        //功能列表
        float modsY = y + 10 + modsWheel;
        float valuesY = y + 40 + valuesWheel;
        if (curModule != null) {
            for (Value<?> v : curModule.getValues()) {
                if (v instanceof BooleanValue) {
                    if (isHovered(x + leftWidth + 10, valuesY, x + width - 40, valuesY + 10, mouseX, mouseY) && mouseButton == 0) {
                        ((BooleanValue) v).setValue(!((BooleanValue) v).getValue());
                    }
                } else if (v instanceof NumberValue) {
                    if (isHovered(x + width - 100, valuesY, x + width - 30, valuesY + 10, mouseX, mouseY) && mouseButton == 0) {
                        ((NumberValue<?>) v).drag = true;
                    }
                } else if (v instanceof ModeValue) {
                    if (isHovered(x + width - 110, valuesY, x + width - 30, valuesY + 10, mouseX, mouseY) && mouseButton == 0) {
                        ((ModeValue) v).setExpanded(!((ModeValue) v).isExpanded());
                    }

                    if (((ModeValue) v).isExpanded()) {
                        for (String mode : ((ModeValue) v).getModes()) {
                            if (isHovered(x + width - 110, valuesY + 10, x + width - 30, valuesY + 20, mouseX, mouseY)) {
                                ((ModeValue) v).setValue(mode);
                                ((ModeValue) v).setExpanded(false);
                            }
                            valuesY += 15;
                        }
                    }
                    valuesY += 5;

                } else if (v instanceof ColorValue) {
                    valuesY += 30;
                }
                valuesY += 20;
            }
            if (Mouse.isButtonDown(1) && curType != ModuleCategory.Settings) {
                curModule = null;
                listAnim = 60;
            }
        } else {
            for (Map.Entry<String, Module> m : Hanabi.INSTANCE.moduleManager.modules.entrySet()) {
                if (m.getValue().type == curType) {
                    if (isHovered(x + leftWidth + 8, Math.max(modsY, y), x + width - 10, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 2) {
                        NotificationsUtils.sendMessage(NotificationType.MESSAGE, "Please type key you want to bind");
                        waitingToBind = true;
                        boundModule = m.getValue();
                    }

                    if (isHovered(x + leftWidth + 8, Math.max(modsY, y), x + width - 8, Math.min(modsY + 30, y + height - 17.5f), mouseX, mouseY) && mouseButton == 0) {
                        if (m.getValue().canBeEnabled) {
                            m.getValue().setStage(!m.getValue().stage);
                            if (mc.theWorld != null) {
                                mc.thePlayer.playSound("random.click", 1, 1);
                            }
                        }
                    }
                    if (isHovered(x + leftWidth + 8, Math.max(modsY, y), x + width - 8, Math.min(modsY + 30, y + height), mouseX, mouseY) && mouseButton == 1) {
                        //打开功能values列表
                        if (curModule != m.getValue() && m.getValue().getValues().size() > 0) {
                            curModule = m.getValue();
                            wheel_temp1 = 0;
                            listAnim = 60;
                        } else {
                            curModule = null;
                        }
                    }
                    modsY += 35;
                }
            }
        }
    }
}
