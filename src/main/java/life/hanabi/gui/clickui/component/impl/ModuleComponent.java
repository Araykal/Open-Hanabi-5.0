package life.hanabi.gui.clickui.component.impl;

import life.hanabi.Hanabi;
import life.hanabi.core.Module;
import life.hanabi.core.values.Value;
import life.hanabi.core.values.values.BooleanValue;
import life.hanabi.core.values.values.ModeValue;
import life.hanabi.core.values.values.NumberValue;
import life.hanabi.gui.clickui.ClickUIScreen;
import life.hanabi.gui.clickui.component.Component;
import life.hanabi.gui.clickui.component.impl.values.BooleanValueComponent;
import life.hanabi.gui.clickui.component.impl.values.ModeValueComponent;
import life.hanabi.gui.clickui.component.impl.values.NumberValueComponent;
import life.hanabi.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class ModuleComponent extends life.hanabi.gui.clickui.component.Component {
    ArrayList<Component> subComponents = new ArrayList<>();

    float x, y;

    public ModuleComponent(Module mod) {
        module = mod;
        for (Value<?> value : module.values) {
            if (value instanceof BooleanValue) {
                subComponents.add(new BooleanValueComponent((BooleanValue) value));
            } else if (value instanceof NumberValue) {
                subComponents.add(new NumberValueComponent((NumberValue) value));
            } else if (value instanceof ModeValue) {
                subComponents.add(new ModeValueComponent((ModeValue) value));
            }
        }
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        float boxwidth = ClickUIScreen.width - ClickUIScreen.leftWidth - (ClickUIScreen.currentModule != null ? ClickUIScreen.rightWidth : 0) - 10;
        RenderUtil.drawRoundedRectUsingCircle(x, y, x + boxwidth, y + 26, 3, new Color(243, 243, 243).getRGB());
        Hanabi.INSTANCE.fontLoaders.syFont18.drawString(module.name, x + 10, y + 10, module.stage ? new Color(0, 0, 0).getRGB() : new Color(131, 131, 131).getRGB());

        RenderUtil.drawRoundedRect2(x + boxwidth - 20, y + 9, x + boxwidth - 6, y + 17, module.stage ? new Color(24, 144, 255).getRGB() : new Color(191, 191, 191).getRGB());
        RenderUtil.drawCircle(x + boxwidth - 16 + (module.stage ? 6 : 0), y + 13, 3, new Color(255, 255, 255).getRGB());
    }

    public void drawSubComponents(float x, float y, float mouseX, float mouseY) {
        for (life.hanabi.gui.clickui.component.Component component : subComponents) {
            component.draw(x, y, mouseX, mouseY);
            y += 20;
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (mouseX >= x && mouseX <= x + ClickUIScreen.width - ClickUIScreen.leftWidth - (ClickUIScreen.currentModule != null ? ClickUIScreen.rightWidth : 0) - 10 && mouseY >= y && mouseY <= y + 27) {
            if (mouseButton == 0) {
                module.stage = !module.stage;
            } else if (mouseButton == 1) {
                if(ClickUIScreen.currentModule == this)
                    ClickUIScreen.currentModule = null;
                else
                    ClickUIScreen.currentModule = this;
            }
        }
    }

    public void mouseClickedSubComponents(int mouseX, int mouseY, int mouseButton) {
        for (Component component : subComponents) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
