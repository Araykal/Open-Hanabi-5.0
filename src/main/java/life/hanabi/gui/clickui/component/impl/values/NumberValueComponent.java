package life.hanabi.gui.clickui.component.impl.values;

import life.hanabi.Hanabi;
import life.hanabi.core.values.values.NumberValue;
import life.hanabi.gui.clickui.ClickUIScreen;
import life.hanabi.gui.clickui.component.Component;
import life.hanabi.utils.render.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class NumberValueComponent extends Component {
    NumberValue<Number> value;

    public NumberValueComponent(NumberValue<Number> value) {
        this.value = value;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY) {

        Hanabi.INSTANCE.fontLoaders.syFont18.drawString(value.name, x, y, new Color(0, 0, 0).getRGB());

        Hanabi.INSTANCE.fontLoaders.syFont18.drawString("-", x, y + 9, new Color(94, 94, 94).getRGB());
        Hanabi.INSTANCE.fontLoaders.syFont18.drawString("+", x + ClickUIScreen.rightWidth - 24, y + 9, new Color(66, 66, 66).getRGB());

        float width = ClickUIScreen.rightWidth - 30;
        RenderUtil.drawRoundedRect2(x + 10, y + 11, x + width, y + 13, new Color(222, 222, 222).getRGB());
        RenderUtil.drawRoundedRect2(x + 10, y + 11, x + 10 + (width * (value.getValue().floatValue() - value.getMin().floatValue()) / (value.getMax().floatValue() - value.getMin().floatValue())), y + 13, new Color(87, 175, 255).getRGB());
        if (RenderUtil.isHovered(x + 10, y + 11, width, 2, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            float percent = (mouseX - (x + 10)) / width;
            value.setValue(value.getMin().floatValue() + (value.getMax().floatValue() - value.getMin().floatValue()) * percent);
        }
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {

    }
}
