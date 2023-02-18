package life.hanabi.gui.clickui.component.impl.values;

import life.hanabi.Hanabi;
import life.hanabi.core.values.values.BooleanValue;
import life.hanabi.gui.clickui.ClickUIScreen;
import life.hanabi.gui.clickui.component.Component;
import life.hanabi.utils.render.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class BooleanValueComponent extends Component {
    BooleanValue value;
    float x,y;
    public BooleanValueComponent(BooleanValue value) {
        this.value = value;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        RenderUtil.drawImage(new ResourceLocation("client/icons/clickgui/" + (value.getValue() ? "enabled" : "disabled") + ".png"), x, y, 8, 8);
        Hanabi.INSTANCE.fontLoaders.syFont18.drawString(value.name, x + 10, y, new Color(0, 0, 0).getRGB());
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (mouseButton == 0 && RenderUtil.isHovered(x + 5, y, 8, 8, mouseX, mouseY)) {
            value.setValue(!value.getValue());
        }
    }
}
